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

(def manifest
  (render-files (get-manifest clojurish-templates)
                {:name "example/app"
                 :ns "example.app"
                 :path "example/app"
                 :project-name "app"}))

(deftest test-render-files
  (is (= (-> manifest keys sort) expected-manifest)))

(deftest test-project-definition
  (let [[_ named version & kvs] (-> manifest (get "project.clj") read-string)
        props (apply hash-map kvs)]
    (is (= named 'example/app))
    (is (= version "0.1.0-SNAPSHOT"))
    (is (= (:uberjar-name props) "example-app-standalone.jar"))
    (is (= (:dependencies props)
           '[[com.stuartsierra/component "0.2.2"]
             [environ "1.0.0"]
             [org.clojure/clojure "1.7.0"]
             [prismatic/schema "0.4.3"]]))))
