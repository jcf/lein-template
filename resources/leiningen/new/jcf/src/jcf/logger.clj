(ns {{ns}}.logger
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as lapp]))

(defrecord Logger [level path stdout? timestamp-format]
  component/Lifecycle
  (start [c]
    (log/info {:component :logger :at :start :path path :stdout? stdout?})
    (log/merge-config!
     {:level level
      :appenders
      {:spit (when path (lapp/spit-appender {:fname path}))
       :println {:enabled? stdout?}}
      :timestamp-opts {:pattern timestamp-format}})
    c)
  (stop [c]
    (log/info {:component :logger :at :stop})
    c))
