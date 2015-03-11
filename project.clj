(defproject jcf/lein-template "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/jcf/lein-template"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :deploy-repositories [["releases" :clojars]]
  :eval-in-leiningen true
  :profiles {:dev {:dependencies [[leiningen "2.5.1"]
                                  [me.raynes/fs "1.4.6"]]}})
