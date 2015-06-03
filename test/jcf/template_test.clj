(ns jcf.template-test
  (:require [clojure.test :refer :all]
            [leiningen.new.jcf :refer :all]
            [schema.test :refer [validate-schemas]]))

(use-fixtures :once validate-schemas)

(def ^:private expected-manifest
  [".gitignore"
   "dev/user.clj"
   "project.clj"
   "src/{{path}}/config.clj"
   "src/{{path}}/main.clj"
   "src/{{path}}/system.clj"
   "system.properties"
   "test/{{path}}/config_test.clj"])

(deftest test-render-files
  (let [manifest (render-files (get-manifest clojurish-templates)
                               {:name "example/app"
                                :ns "example.app"
                                :path "example/app"
                                :project-name "app"})]
    (is (= (-> manifest keys sort) expected-manifest))))
