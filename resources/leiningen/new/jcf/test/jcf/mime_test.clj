(ns {{ns}}.mime-test
  (:require [{{ns}}.mime :as sut]
            [clojure.test :refer :all]))

(def ^:private mime-tests
  {"text/plain"
   {:charset nil
    :encoding "plain"
    :params {}
    :subtype "plain"
    :type "text"}

   "text/javascript"
   {:charset nil
    :encoding "javascript"
    :params {}
    :subtype "javascript"
    :type "text"}

   "text/html;charset=utf-8"
   {:charset "utf-8"
    :encoding "html"
    :params {"charset" "utf-8"}
    :subtype "html"
    :type "text"}

   "application/json"
   {:charset nil
    :encoding "json"
    :params {}
    :subtype "json"
    :type "application"}

   "application/vnd.vimeo.user+json; charset=utf-8"
   {:charset "utf-8"
    :encoding "json"
    :params {"charset" "utf-8"}
    :subtype "vnd.vimeo.user+json"
    :type "application"}})

(deftest t-parse-mime
  (doseq [[s m] mime-tests]
    (is (= m (sut/parse-mime s)))))
