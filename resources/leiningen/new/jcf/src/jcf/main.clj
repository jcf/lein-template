(ns {{ns}}.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [{{ns}}.config :as config]
            [{{ns}}.system :refer [new-system]]))

(defn -main []
  (-> env
      config/config-map
      new-system
      component/start-system))
