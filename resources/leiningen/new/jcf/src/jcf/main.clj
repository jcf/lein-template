(ns {{ns}}.main
  (:gen-class)
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [{{ns}}
             [http-client :as http-client]
             [logger :as logger]]))

;; -----------------------------------------------------------------------------
;; Configuration

(def ^:private profiles
  {"dev" :dev
   "test" :test
   "uberjar" :uberjar})

(defn- configure
  [system profile]
  (let [path (io/resource "config.edn")
        _ (assert path "config.edn not found on resource path.")
        config (aero/read-config path {:profile profile})]
    (merge-with merge system config)))

;; -----------------------------------------------------------------------------
;; System

(defn- new-system-map
  []
  (component/system-map
   :http (http-client/map->HTTP {})
   :logger (logger/map->Logger {})))

(defn new-system
  ([]
   (new-system :dev))
  ([profile]
   (configure (new-system-map) profile)))

(defn -main
  [profile]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ thread exception]
       (log/error {:thread (.getName thread) :exception exception}))))

  (if-let [k (get profiles profile)]
    (do
      (log/info {:msg "System coming right up..." :profile k})
      (component/start-system (new-system k)))
    (do
      (println
       (format "Profile must be one of %s. Got %s. Exiting.")
       (keys profiles) profile)
      (System/exit 1))))
