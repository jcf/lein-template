(ns jcf.template-test
  (:require [clojure.test :refer :all]
            [leiningen.new.jcf :as sut]))

(def ^:private expected-manifest
  [".gitignore"
   "dev/user.clj"
   "project.clj"
   "src/{{path}}/config.clj"
   "src/{{path}}/main.clj"
   "system.properties"
   "test/{{path}}/config_test.clj"])

(def manifest
  (sut/render-files (sut/get-manifest sut/clojurish-templates)
                    {:name "example/app"
                     :ns "example.app"
                     :path "example/app"
                     :project-name "app"
                     :hyphenated-name "example-app"}))

(deftest test-name->data
  (are [in out] (= (sut/name->data in) out)
    "foo"
    {:hyphenated-name "foo"
     :name "foo"
     :ns "foo"
     :path "foo"
     :project-name "foo"}

    "foo.app"
    {:hyphenated-name "foo.app"
     :name "foo.app"
     :ns "foo.app"
     :path "foo/app"
     :project-name "foo.app"}

    "foo/app"
    {:hyphenated-name "foo-app"
     :name "foo/app"
     :ns "foo.app"
     :path "foo/app"
     :project-name "app"}

    "foo/bar/app"
    {:hyphenated-name "foo-bar-app"
     :name "foo/bar/app"
     :ns "foo.bar.app"
     :path "foo/bar/app"
     :project-name "app"}))

(deftest test-render-files
  (is (= (-> manifest keys sort) expected-manifest)))

(deftest test-project-definition
  (let [[_ named version & kvs] (-> manifest (get "project.clj") read-string)
        props (apply hash-map kvs)]
    (is (= named 'example/app))
    (is (= version "0.1.0-SNAPSHOT"))
    (is (= (:repl-options props) '{:init-ns user}))
    (is (= (:uberjar-name props) "example-app-standalone.jar"))
    (is (= (:dependencies props)
           '[[com.stuartsierra/component "0.3.1"]
             [environ "1.0.2"]
             [org.clojure/clojure "1.8.0"]
             [prismatic/schema "1.0.5"]]))
    (is (= (get-in props [:profiles :dev :dependencies])
           '[[org.clojure/tools.namespace "0.2.10"]
             [reloaded.repl "0.2.1"]]))))
