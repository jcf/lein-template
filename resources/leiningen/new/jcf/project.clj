(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [environ "1.0.0"]
                 [listora/constraint-config "0.2.0"]
                 [org.clojure/clojure "1.6.0"]]
  :min-lein-version "2.5.0"
  :uberjar-name "{{project-name}}-standalone.jar"
  :profiles
  {:dev {:dependencies [[org.clojure/tools.namespace "0.2.5"]
                        [reloaded.repl "0.1.0"]]
         :source-paths ["dev"]}
   :uberjar {:aot :all
             :main {{ns}}.main
             :omit-source true}})
