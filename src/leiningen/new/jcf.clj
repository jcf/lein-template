(ns leiningen.new.jcf
  (:require [clojure.string :as str]
            [leiningen.core.main :as main]
            [leiningen.new.templates :as tmpl]))

(defn- hyphenated-name
  [s]
  (str/replace s #"/" "-"))

(def ^:private render
  (tmpl/renderer "jcf"))

(def clojurish-templates
  "All templates, with the exception of `gitignore` which needs to be 'hidden'
  from git."
  ["dev/user.clj"
   "project.clj"
   "src/jcf/config.clj"
   "src/jcf/main.clj"
   "system.properties"
   "test/jcf/config_test.clj"])

(defn- expand-paths
  "Take a list of template paths, and expand them into a map of destination
  mapped to template path.

  Any instance of `jcf` in the template path will be replaced with
  `{{path}}`."
  [paths]
  (->> paths
       (map (juxt #(.replace ^String % "jcf" "{{path}}") identity))
       (into {})))

(defn get-manifest
  [paths]
  (assoc (expand-paths paths) ".gitignore" "gitignore"))

(defn render-files
  "Given a list of destinations and template paths, maps over the template paths
  and renders each file using the `jcf` renderer."
  [files data]
  (reduce-kv #(assoc %1 %2 (render %3 data)) {} files))

(defn name->data
  [named]
  {:hyphenated-name (hyphenated-name named)
   :name named
   :ns (tmpl/sanitize-ns named)
   :path (tmpl/name-to-path named)
   :project-name (tmpl/project-name named)})

(defn jcf
  [named]
  (let [data (name->data named)
        files (get-manifest clojurish-templates)]
    (main/info (format "Generating %d files..." (count files)))
    (apply tmpl/->files data (render-files files data))))
