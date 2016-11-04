(ns jcf.integration-test
  (:require [clojure.java.shell :refer [sh]]
            [clojure
             [test :refer :all]
             [string :as str]]
            [leiningen.new
             [jcf :refer :all]
             [templates :refer [*dir*]]]
            [me.raynes.fs :as fs :refer [temp-dir]]))

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
            (fs/delete-dir sandbox)))))))

(use-fixtures :once generate-project)

(def ^:private commands
  [["lein" "ancient"]
   ;; Eastwood doesn't support Clojure 1.9, and may become somewhat redundant if
   ;; we add enough specs to Clojure itself.
   ;;
   ;; https://github.com/jonase/eastwood/issues/201
   ;;
   ;; ["lein" "eastwood"]
   ["lein" "test"]
   ["lein" "whitespace-linter"]])

(deftest t-lein-commands
  (doseq [args commands :let [command-string (str/join " " args)]]
    (testing command-string
      (let [{:keys [exit out err]} (apply sh (conj args :dir *dir*))]
        (when (is (zero? exit)
                  (str/join
                   "\n\n"
                   (filter identity
                           [(str command-string " failed with status " exit ".")
                            (when err (str "Err:\n" err))
                            (when out (str "Out:\n" out))])))
          (let [our-errors (->> err
                                str/split-lines
                                (filter #(re-find (re-pattern app-name) %)))]
            (is (empty? our-errors)
                (str "Warnings featuring \""
                     app-name
                     "\" found in stdout"))))))))
