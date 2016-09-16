(ns {{ns}}.http-client
  (:require [aleph.http :as http]
            byte-streams
            [cheshire.core :as json]
            [clojure.core.match :refer [match]]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as deferred]
            [ring.util
             [codec :as codec]
             [response :as response]]
            [io.pedestal.log :as log]
            [{{ns}}
             [common :as common]
             [mime :as mime]]))

(defn parse-response
  [response]
  (let [ctype (response/get-header response "content-type")
        mime (mime/parse-mime ctype)]
    (log/debug :task ::parse-response :content-type ctype :mime mime)
    (update
     response :body
     (match [mime]
       [{:encoding (:or "javascript" "json")}]
       (comp common/hyphenate-keys
             json/parse-stream
             byte-streams/to-reader)

       [{:type "text"}]
       byte-streams/to-string

       :else identity))))

(defprotocol IRequest
  (request [this options]))

(defrecord HTTP [connection-timeout
                 pool-timeout
                 request-timeout]
  component/Lifecycle
  (start [c]
    (log/info :component :http :at :start)
    (assoc c :pool http/default-connection-pool))
  (stop [c]
    (log/info :component :http :at :stop)
    c)

  IRequest
  (request [this options]
    (log/debug :task ::request :request options)
    (deferred/chain
      (-> {:connection-timeout connection-timeout
           :pool (:pool this)
           :pool-timeout pool-timeout
           :request-timeout request-timeout
           :throw-exceptions? false}
          (merge options)
          http/request)
      parse-response
      (fn log-final-response
        [response]
        (log/trace :task ::request :response response)
        response))))
