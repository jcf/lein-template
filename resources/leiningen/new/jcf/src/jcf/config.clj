(ns {{ns}}.config
  (:import [clojure.lang Keyword])
  (:require [constraint.config :as conf]
            [constraint.core :as constraint :refer [?]]
            [environ.core :refer [env]]))

(def ^:private defaults
  {:log-level :debug
   :port 3000})

(def ^:private definition
  {:log-level Keyword
   :port Long
   :user String})

(defn- read-key [k]
  (if (constraint/optional? k) (.constraint k) k))

(defn config-map
  ([] (config-map env))
  ([m]
   (let [keys-in-definition (map read-key (keys definition))
         config (->> (select-keys m keys-in-definition)
                     (merge defaults))]
     (conf/verify-config definition config))))
