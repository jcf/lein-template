(defproject jcf/lein-template "0.9.0-SNAPSHOT"
  :description "A Leiningen template I use for quickly creating a reloadable, REPL-driven Clojure app."
  :url "https://github.com/jcf/lein-template"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :deploy-repositories [["releases" :clojars]]
  :eval-in-leiningen true
  :profiles
  {:dev
   {:dependencies [[leiningen "2.5.1"]
                   [me.raynes/fs "1.4.6"]
                   [org.clojure/test.check "0.9.0"]]
    :plugins [[jonase/eastwood "0.2.3"]
              [lein-ancient "0.6.10"]
              [listora/whitespace-linter "0.1.0"]]}})
