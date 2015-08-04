(ns {{ns}}.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [{{ns}}.config :as config]
            [{{ns}}.system :refer [new-system]]))

(defn -main []
  (-> config/config-map
      new-system
      component/start-system))
