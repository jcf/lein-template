(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :dependencies [[aero "1.0.0-beta2"]
                 [aleph "0.4.2-alpha3"]
                 [cheshire "5.6.1"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.4.0-alpha1"]
                 [inflections "0.12.1"]
                 [medley "0.8.1"]
                 [org.clojure/clojure "1.9.0-alpha4"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [ring/ring-codec "1.0.0"]
                 [ring/ring-core "1.4.0"]]
  :main {{ns}}.main
  :min-lein-version "2.5.0"
  :repl-options {:init-ns user}
  :uberjar-name "{{hyphenated-name}}-standalone.jar"
  :profiles
  {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                        [org.clojure/tools.namespace "0.2.10"]
                        [reloaded.repl "0.2.1"]]
         :source-paths ["dev"]}
   :uberjar {:aot :all
             :omit-source true}})
