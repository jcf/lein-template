(ns jcf.system-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [jcf.system :refer :all]))

(deftest test-system
  (let [sys (component/start-system new-system)]
    (is (map? sys))
    (is (component/stop-system sys))))
