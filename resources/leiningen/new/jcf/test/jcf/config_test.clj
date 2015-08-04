(ns {{ns}}.config-test
  (:require [{{ns}}.config :refer :all]
            [clojure.test :refer :all]))

(def ^:private example-env
  {:ignored "ignored"
   :port "5000"})

(deftest test-config-map
  (let [config (config-map example-env)]
    (is (= (-> config keys sort) [:port]))
    (are [k v] (= (get config k ::missing) v)
      :port 5000)))
