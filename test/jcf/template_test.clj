(ns jcf.template-test
  (:require [clojure
             ;; [spec :as s]
             [string :as str]
             [test :refer :all]]
            [clojure.java.io :as io]
            [leiningen.new.jcf :as sut]
            [me.raynes.fs :as fs :refer [*cwd*]]
            [clojure.set :as set]
            [clojure.data :as data])
  (:import java.io.File))

;; (use-fixtures :once (fn [f] (s/instrument-all) (f)))

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

(defn- str-remove-prefix
  [prefix s]
  (if (str/starts-with? s prefix)
    (subs s (count prefix))
    s))

;; -----------------------------------------------------------------------------
;; Fixtures

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
  (let [files (->> "leiningen/new/jcf" io/resource io/file file-seq)
        manifest (-> manifest keys set)
        expected-manifest
        (->> files
             (remove (fn [^java.io.File file]
                       (or (.isDirectory file)
                           (#{".DS_Store" "gitignore"} (.getName file)))))
             (map (fn [^java.io.File file]
                    (str-remove-prefix (str (.getPath ^java.io.File *cwd*)
                                            "/resources/leiningen/new/jcf/")
                                       (.getPath file))))
             (map #(str/replace % #"jcf" "{{path}}"))
             (cons ".gitignore")
             set)]
    (is (= expected-manifest manifest)
        (let [[a b _] (data/diff expected-manifest manifest)]
          (str/join
           "\n\n"
           (filter identity
                   [(when a (str "Not in manifest:\n" a))
                    (when b (str "Unexpected on manifest:\n" b))]))))))

(deftest test-project-definition
  (let [[_ named version & kvs] (-> manifest (get "project.clj") read-string)
        props (apply hash-map kvs)]
    (is (= named 'example/app))
    (is (= version "0.1.0-SNAPSHOT"))
    (is (= (:repl-options props) '{:init-ns user}))
    (is (= (:uberjar-name props) "example-app-standalone.jar"))
    (is-ordered (map dep-name (:dependencies props)))
    (is-ordered (map dep-name (get-in props [:profiles :dev :dependencies])))))
