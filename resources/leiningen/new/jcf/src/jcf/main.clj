(ns {{ns}}.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [{{ns}}.config :as config]))

(defn new-system [config]
  (component/system-map :config-options config))

(defn -main []
  (-> env
      config/config-map
      new-system
      component/start-system))
