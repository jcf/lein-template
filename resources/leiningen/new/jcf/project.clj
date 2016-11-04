(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :dependencies [[aero "1.0.1"]
                 [aleph "0.4.2-alpha8"]
                 [ch.qos.logback/logback-classic "1.1.7"
                  :exclusions [org.slf4j/slf4j-api]]
                 [cheshire "5.6.3"]
                 [com.stuartsierra/component "0.3.1"]
                 [inflections "0.12.2"
                  :exclusions [commons-codec]]
                 [io.pedestal/pedestal.log "0.5.1"]
                 [medley "0.8.3"]
                 [org.clojure/clojure "1.9.0-alpha12"]
                 [org.clojure/core.async "0.2.395"
                  :exclusions [org.clojure/tools.reader]]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.slf4j/jcl-over-slf4j "1.7.21"]
                 [org.slf4j/jul-to-slf4j "1.7.21"]
                 [org.slf4j/log4j-over-slf4j "1.7.21"]
                 [ring/ring-codec "1.0.1"]
                 [ring/ring-core "1.5.0"]]
  :main {{ns}}.main
  :min-lein-version "2.5.0"
  :pedantic? :abort
  :repl-options {:init-ns user}
  :uberjar-name "{{hyphenated-name}}-standalone.jar"
  :profiles
  {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                        [org.clojure/tools.namespace "0.2.10"]
                        [reloaded.repl "0.2.3"
                         :exclusions [org.clojure/tools.namespace
                                      org.clojure/tools.reader]]]
         :plugins [[jonase/eastwood "0.2.3"]
                   [lein-ancient "0.6.10"]
                   [listora/whitespace-linter "0.1.0"]]
         :source-paths ["dev"]}
   :uberjar {:aot :all
             :omit-source true}})
