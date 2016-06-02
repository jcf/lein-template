(ns jcf.template-test
  (:require [clojure.test :refer :all]
            [leiningen.new.jcf :as sut]))

;; -----------------------------------------------------------------------------
;; Utils

(defmacro ^:private is-ordered
  "Naive ordered check that is extremely inefficient, but that supports
  comparing any sortable."
  [coll]
  `(is (= ~coll (sort ~coll))))

(defn- dep-name
  [dep]
  (-> dep first str))

;; -----------------------------------------------------------------------------
;; Fixtures

(def ^:private expected-manifest
  [".gitignore"
   "dev/user.clj"
   "project.clj"
   "resources/config.edn"
   "src/{{path}}/common.clj"
   "src/{{path}}/http_client.clj"
   "src/{{path}}/logger.clj"
   "src/{{path}}/main.clj"
   "src/{{path}}/mime.clj"
   "system.properties"
   "test/{{path}}/common_test.clj"
   "test/{{path}}/http_client_test.clj"
   "test/{{path}}/mime_test.clj"
   "test/{{path}}/test/util.clj"])

(def manifest
  (sut/render-files (sut/get-manifest sut/clojurish-templates)
                    {:name "example/app"
                     :ns "example.app"
                     :path "example/app"
                     :project-name "app"
                     :hyphenated-name "example-app"}))

;; -----------------------------------------------------------------------------
;; Tests

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
    (is-ordered (map dep-name (:dependencies props)))
    (is-ordered (map dep-name (get-in props [:profiles :dev :dependencies])))))
