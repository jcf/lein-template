(ns user
  (:require [clojure.java.io :as io]
            [clojure.java.javadoc :refer [javadoc]]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.test :as test]
            [environ.core :refer [env]]
            [reloaded.repl :refer [go init reset start stop system]]
            [{{ns}}.config :refer [config-map]]
            [{{ns}}.system :refer [new-system]]))

(reloaded.repl/set-init! #(new-system (config-map env)))
