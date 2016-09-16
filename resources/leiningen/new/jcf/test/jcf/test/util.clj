(ns {{ns}}.test.util
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [{{ns}}
             [http-client :as http-client]
             [main :as main]]))

;; -----------------------------------------------------------------------------
;; Component lifecycle

(defmacro with-system
  [bindings & body]
  {:pre [(even? (count bindings))]}
  `(let [running# (component/start-system ~(second bindings))
         ~(first bindings) running#]
     (try
       ~@body
       (catch InterruptedException _#
         (Thread/interrupted))
       (finally
         (component/stop-system running#)))))

;; -----------------------------------------------------------------------------
;; Fixtures

(defn read-resource
  "Reads the contents of a file on the resource path at `path`. If the file does
  not exist an assertion error is thrown."
  [path]
  (if-let [resource (io/resource path)]
    (-> resource slurp read-string)
    (throw (IllegalArgumentException. (str path " does not exist!")))))

(defn read-fixture
  [s]
  (read-resource (str "{{path}}/fixtures/" s)))

;; -----------------------------------------------------------------------------
;; Mockable test system

(defn mock-http
  [responder]
  (reify
    http-client/IRequest
    (request [_ m]
      (responder m))))

(defn new-test-system
  []
  (main/new-system :test))
