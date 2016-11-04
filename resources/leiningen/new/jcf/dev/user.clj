(ns user
  (:require [reloaded.repl :refer [go init reset start stop system]]
            [{{ns}}.main :as main]))

(reloaded.repl/set-init! #(main/new-system :dev))
