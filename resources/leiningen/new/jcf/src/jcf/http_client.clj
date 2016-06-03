(ns {{ns}}.http-client
  (:require [aleph.http :as http]
            byte-streams
            [cheshire.core :as json]
            [clojure.core.match :refer [match]]
            [clojure.spec :as s]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as deferred]
            [ring.util
             [codec :as codec]
             [response :as response]]
            [taoensso.timbre :as log]
            [{{ns}}
             [common :as common]
             [mime :as mime]]))

;; -----------------------------------------------------------------------------
;; Spec

(s/def ::body ::s/any)
(s/def ::headers (s/map-of string? string?))
(s/def ::response-time integer?)
(s/def ::status (s/and integer? #(<= 200 % 599)))

(s/def ::response
  (s/keys :req-un [::body ::headers ::response-time ::status]))

(s/def ::request-method
  #{:connect :delete :get :head :options :patch :post :put :trace})

(defn- http-url?
  [^String s]
  (let [uri (java.net.URI. s)]
    (and (.getAuthority uri)
         (#{"http" "https"} (.getScheme uri)))))

(s/def ::url
  (s/and string? http-url?))

(s/def ::request-options
  (s/keys :req-un [::request-method ::url]
          :opt-un [::body ::middleware ::multipart ::pool]))

;; -----------------------------------------------------------------------------
;; Parse response

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

(s/fdef parse-response
  :args (s/cat :response ::response)
  :ret ::response)

;; -----------------------------------------------------------------------------
;; IRequest

(defprotocol IRequest
  (request [this options]))


(defrecord HTTP [connection-timeout
                 pool-timeout
                 request-timeout]
  component/Lifecycle
  (start [c]
    (log/info {:component :http :at :start})
    (assoc c :pool http/default-connection-pool))
  (stop [c]
    (log/info {:component :http :at :stop})
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
