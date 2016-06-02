(ns leiningen.new.jcf
  (:require [clojure
             [spec :as s]
             [string :as str]]
            [leiningen.core.main :as main]
            [leiningen.new.templates :as tmpl]))

;; -----------------------------------------------------------------------------
;; Specs

(s/def ::template-path string?)
(s/def ::templates (s/coll-of ::template-path #{}))
(s/def ::template-map (s/map-of ::template-path string?))

(s/def ::hyphenated-name (s/and string? #(not (str/includes? % "/"))))
(s/def ::name string?)
(s/def ::ns string?)
(s/def ::path string?)
(s/def ::project-name string?)
(s/def ::template-data
  (s/keys :req-un [::hyphenated-name ::name ::ns ::path ::project-name]))

;; -----------------------------------------------------------------------------
;; Implementation

(def clojurish-templates
  "All templates, with the exception of `gitignore` which needs to be 'hidden'
  from git."
  #{"dev/user.clj"
    "project.clj"
    "resources/config.edn"
    "src/jcf/common.clj"
    "src/jcf/http_client.clj"
    "src/jcf/logger.clj"
    "src/jcf/main.clj"
    "src/jcf/mime.clj"
    "system.properties"
    "test/jcf/mime_test.clj"
    "test/jcf/common_test.clj"
    "test/jcf/http_client_test.clj"
    "test/jcf/test/util.clj"})

(defn- hyphenated-name
  [s]
  (str/replace s #"/" "-"))

(s/fdef hyphenated-name
  :args (s/cat :s string?)
  :fn #(not (str/includes? (:ret %) "/"))
  :ret ::hyphenated-name)

(def ^:private render
  (tmpl/renderer "jcf"))

(defn- expand-paths
  "Take a list of template paths, and expand them into a map of destination
  mapped to template path.

  Any instance of `jcf` in the template path will be replaced with
  `{{path}}`."
  [paths]
  (->> paths
       (map (juxt #(.replace ^String % "jcf" "{{path}}") identity))
       (into {})))

(s/fdef expand-paths
  :args (s/cat :paths ::templates)
  :ret ::template-map)

(defn get-manifest
  [paths]
  (assoc (expand-paths paths) ".gitignore" "gitignore"))

(s/fdef get-manifest
  :args (s/cat :paths ::templates)
  :ret ::template-map)

;; NOTE Do not spec `render-files`.
;;
;; Doing so will result in fake template paths that don't exist, and when
;; Leiningen encounters them it kills the REPL.
;;
;;    Template resource 'leiningen/new/jcf/Q' not found.
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

(s/fdef name->data
  :args (s/cat :named ::name)
  :ret ::template-data)

;; NOTE Do not spec `jcf`.
;;
;; Doing so will result in us generating a load of new test projects.
(defn jcf
  [named]
  (let [data (name->data named)
        files (get-manifest clojurish-templates)]
    (main/info (format "Generating %d files..." (count files)))
    (main/info (str/join "\n" (sort (keys files))))
    (apply tmpl/->files data (render-files files data))))
