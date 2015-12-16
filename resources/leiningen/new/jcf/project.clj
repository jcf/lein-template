(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [environ "1.0.0"]
                 [org.clojure/clojure "1.7.0"]
                 [prismatic/schema "0.4.3"]]
  :main {{ns}}.main
  :min-lein-version "2.5.0"
  :uberjar-name "{{hyphenated-name}}-standalone.jar"
  :profiles
  {:dev {:dependencies [[org.clojure/tools.namespace "0.2.5"]
                        [reloaded.repl "0.1.0"]]
         :source-paths ["dev"]}
   :uberjar {:aot :all
             :omit-source true}})
