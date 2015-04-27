(ns user
  (:require [environ.core :refer [env]]
            [reloaded.repl :refer [go init reset start stop system]]
            [{{ns}}.config :refer [config-map]]
            [{{ns}}.system :refer [new-system]]))

(reloaded.repl/set-init! #(new-system (config-map env)))
