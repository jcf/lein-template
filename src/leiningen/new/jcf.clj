(ns leiningen.new.jcf
  (:require [leiningen.core.main :as main]
            [leiningen.new.templates :as tmpl]
            [schema.core :as s]))

(def ^:private render
  (tmpl/renderer "jcf"))

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

(def ^:private Templates
  {s/Str s/Str})

(s/defn ^:private expand-paths :- Templates
  "Take a list of template paths, and expand them into a map of destination
  mapped to template path.

  Any instance of `jcf` in the template path will be replaced with
  `{{path}}`."
  [paths :- [s/Str]]
  (->> paths
       (map (juxt #(.replace % "jcf" "{{path}}") identity))
       (into {})))

(s/defn get-manifest :- Templates
  [paths :- [s/Str]]
  (assoc (expand-paths paths) ".gitignore" "gitignore"))

(s/defn render-files :- Templates
  "Given a list of destinations and template paths, maps over the template paths
  and renders each file using the `jcf` renderer."
  [files :- Templates data :- {s/Keyword s/Str}]
  (reduce-kv #(assoc %1 %2 (render %3 data)) {} files))

(s/defn jcf
  [name :- s/Str]
  (let [data {:name name
              :ns (tmpl/sanitize-ns name)
              :path (tmpl/name-to-path name)
              :project-name (tmpl/project-name name)}
        files (get-manifest clojurish-templates)]
    (main/info (format "Generating %d files..." (count files)))
    (apply tmpl/->files data (render-files files data))))
