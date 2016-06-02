(ns {{ns}}.common
  (:require byte-streams
            [clojure
             [string :as str]
             [walk :as walk]]
            [inflections.core :as infl]))

(def bais
  (comp byte-streams/to-input-stream byte-streams/to-byte-array))

;; -----------------------------------------------------------------------------
;; Inflections

(defn string->keyword
  [s]
  (-> s infl/hyphenate keyword))

(defn keyword->string
  [x]
  (if (keyword? x)
    (->> [(namespace x) (name x)]
         (remove nil?)
         (str/join "/"))
    x))

(defn walk-map
  "Recursively apply a function to all map entries. When map is nil returns an
  empty map."
  [f m]
  (if m
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)
    {}))

(defn underscore-keys
  "Recursively transforms all map keys from hyphenated keywords, to underscored
  strings."
  [m]
  (walk-map (fn [[k v]] [(infl/underscore (keyword->string k)) v]) m))

(defn- hyphenated-keyword
  [x]
  (if (or (string? x) (keyword? x))
    (-> x keyword->string infl/hyphenate keyword)
    x))

(defn hyphenate-keys
  "Recursively transforms all map keys from underscored strings to hyphenated
  keywords."
  [m]
  (walk-map (fn [[k v]] [(hyphenated-keyword k) v]) m))
