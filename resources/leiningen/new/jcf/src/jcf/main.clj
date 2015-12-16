(ns {{ns}}.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [{{ns}}.config :as config]))

(defn new-system [config]
  (component/system-map :config-options config))

(defn -main []
  (-> config/config-map
      new-system
      component/start-system))
