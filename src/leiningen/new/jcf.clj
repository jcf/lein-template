(ns leiningen.new.jcf
  (:require [leiningen.core.main :as main]
            [leiningen.new.templates :refer [->files
                                             name-to-path
                                             project-name
                                             renderer
                                             sanitize-ns]]))

(def render (renderer "jcf"))

(def clojurish-templates
  "All templates, with the exception of `gitignore` which needs to be 'hidden'
  from git."
  ["dev/user.clj"
   "project.clj"
   "src/jcf/config.clj"
   "src/jcf/main.clj"
   "src/jcf/system.clj"
   "system.properties"
   "test/jcf/config_test.clj"])

(defn- expand-paths
  "Take a list of template paths, and expand them into a map of destination
  mapped to template path.

  Any instance of `jcf` in the template path will be replaced with
  `{{path}}`."
  [paths]
  (->> paths
       (map (juxt #(.replace % "jcf" "{{path}}") identity))
       (into {})))

(defn- get-manifest [paths]
  (assoc (expand-paths paths) ".gitignore" "gitignore"))

(defn- render-files
  "Given a list of destinations and template paths, maps over the template paths
  and renders each file using the `jcf` renderer."
  [files data]
  (reduce-kv #(assoc %1 %2 (render %3 data)) {} files))

(defn jcf
  [name]
  (let [data {:name name
              :ns (sanitize-ns name)
              :path (name-to-path name)
              :project-name (project-name name)}
        files (get-manifest clojurish-templates)]
    (main/info (format "Generating %d files..." (count files)))
    (apply ->files data (render-files files data))))
