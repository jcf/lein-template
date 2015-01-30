(ns {{ns}}.system
  (:require [com.stuartsierra.component :as component]))

(defn new-system [config]
  (component/system-map :config-options config))
