(ns {{ns}}.config-test
  (:require [clojure.test :refer :all]
            [{{ns}}.config :refer :all]))

(def ^:private required-keys
  "All configuration options that are required, and have no default value."
  [:user])

(def ^:private example-env
  {:log-level "info"
   :port "5000"
   :user "bob"})

(deftest test-transformation
  (let [config (config-map example-env)]
    (is (= (-> config keys sort) [:log-level :port :user]))

    (are [kseq v] (= (get-in config kseq) v)
         [:log-level] :info
         [:port] 5000
         [:user] "bob")))

(deftest test-missing-required-keys
  (doseq [required-key required-keys]
    (testing (str "without " required-key)
      (let [config (dissoc example-env required-key)]
        (is (thrown? clojure.lang.ExceptionInfo (config-map config)))))))
