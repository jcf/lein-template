(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :dependencies [[com.stuartsierra/component "0.3.0"]
                 [environ "1.0.1"]
                 [org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.1"]]
  :main {{ns}}.main
  :min-lein-version "2.5.0"
  :uberjar-name "{{hyphenated-name}}-standalone.jar"
  :profiles
  {:dev {:dependencies [[org.clojure/tools.namespace "0.2.10"]
                        [reloaded.repl "0.2.1"]]
         :source-paths ["dev"]}
   :uberjar {:aot :all
             :omit-source true}})
