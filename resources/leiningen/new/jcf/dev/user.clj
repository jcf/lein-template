(ns user
  (:require [environ.core :refer [env]]
            [reloaded.repl :refer [go init reset start stop system]]
            [{{ns}}.config :as config]
            [{{ns}}.main :as main]))

(reloaded.repl/set-init! #(-> env config/config-map main/new-system))
