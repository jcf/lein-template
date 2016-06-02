(ns {{ns}}.http-client-test
  (:require [clojure.core.match :refer [match]]
            [clojure.test :refer :all]
            [manifold.deferred :as deferred]
            [{{ns}}
             [common :as common]
             [http-client :as sut]]
            [{{ns}}.test.util :as t]))

(def ^:private mock-http
  (t/mock-http
   #(deferred/chain
      (deferred/future
        (match [%]
          [{:url "https://example.com/"}]
          {:headers {"content-type" "text/plain"}
           :body (common/bais "Hello world!")}))
      sut/parse-response)))

(deftest t-request
  (is (= "Hello world!"
         (-> mock-http
             (sut/request {:request-method :get
                           :url "https://example.com/"})
             deref
             :body))))
