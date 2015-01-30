(ns jcf.template-test
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [leiningen.new.jcf :refer :all]
            [leiningen.new.templates :refer [*dir*]]
            [me.raynes.fs :refer [temp-dir]]))

(def ^:private app-name
  "example")

(def ^:private expected-manifest
  [".gitignore"
   "dev/user.clj"
   "project.clj"
   "src/example/config.clj"
   "src/example/main.clj"
   "src/example/system.clj"
   "system.properties"
   "test/example/config_test.clj"])

(defn- find-files [dir s]
  (let [divider (re-pattern (format "/%s/" s))
        remove-temp-dir #(second (str/split % divider 2))]
    (->> dir
         io/file
         file-seq
         (filter #(.isFile %))
         (map (comp remove-temp-dir str))
         sort)))

(defn- test-cruft? [s]
  (or (= s ".lein-failures") (.startsWith s "target")))

(defn generate-project [test-fn]
  (let [sandbox (temp-dir "jcf-")]
    (binding [*dir* (str sandbox "/" app-name)]
      (println (format "Generating project in %s..." *dir*))
      (jcf app-name)
      (try
        (test-fn)
        (finally
          (when (.isDirectory sandbox)
            (println (format "Deleting project in %s..." *dir*))
            (.delete sandbox)))))))

(use-fixtures :once generate-project)

(deftest test-gitignore-sorted
  (let [ignores (str/split-lines (slurp (str *dir* "/.gitignore")))]
    (is (= ignores (sort ignores)))))

(deftest test-files-exist
  (let [files (->> (find-files *dir* app-name)
                   (remove test-cruft?))]
    (is (= files expected-manifest)
        (format "Unexpected file manifest.\n\nActual:\n%s\n\nExpected:\n%s\n\n"
                (str/join "\n" files)
                (str/join "\n" expected-manifest)))))

(deftest test-lein-test
  (let [_ (println "Running lein test. This'll take a couple of seconds...")
        {:keys [exit out err]} (sh "lein" "test" :dir *dir*)]
    (is (zero? exit)
        (format "lein test failed with status %d.\nOut:\n%s\n\nErr:\n%s\n\n"
                exit out err))))
