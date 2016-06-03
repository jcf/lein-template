(ns jcf.integration-test
  (:require [clojure.java.shell :refer [sh]]
            [clojure.test :refer :all]
            [leiningen.new
             [jcf :refer :all]
             [templates :refer [*dir*]]]
            [me.raynes.fs :refer [temp-dir]]
            [clojure.string :as str]))

(def ^:private app-name
  "example")

(defn generate-project [test-fn]
  (let [sandbox ^java.io.File (temp-dir "jcf-")]
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

(deftest test-lein-test
  (let [_ (println "Running lein test. This'll take a couple of seconds...")
        {:keys [exit out err]} (sh "lein" "test" :dir *dir*)]
    (when (is (zero? exit)
              (str/join
               "\n\n"
               (filter identity
                       [(str "lein test failed with status " exit ".")
                        (str "Dir: " *dir*)
                        (when err (str "Err:\n" err))
                        (when out (str "Out:\n" out))])))
      (let [our-errors (->> err
                            str/split-lines
                            (filter #(re-find (re-pattern app-name) %)))]
        (is (empty? our-errors)
            (str "Warnings featuring \"" app-name "\" found in stdout"))))))
