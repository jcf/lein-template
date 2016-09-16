(ns {{ns}}.common
  (:require byte-streams
            [clojure
             [spec :as s]
             [string :as str]
             [walk :as walk]]
            [clojure.spec.gen :as gen]
            [inflections.core :as infl])
  (:import java.io.ByteArrayInputStream))

;; -----------------------------------------------------------------------------
;; Spec

(s/def ::any-map
  (s/map-of (s/nilable any?) (s/nilable any?)))

(s/def ::maybe-any-map
  (s/nilable ::any-map))

(s/def ::bais
  #(instance? ByteArrayInputStream %))

(defn- convertible?
  [x to]
  (some? (byte-streams/conversion-path x to)))

(s/def ::baisable
  #(convertible? % ByteArrayInputStream))

(s/def ::kv
  (s/tuple any? any?))

;; -----------------------------------------------------------------------------
;; ByteArrayInputStream

(s/fdef bais
  :args (s/cat :x ::baisable)
  :ret ::bais)

(defn bais
  [x]
  {:pre [(s/valid? ::baisable x)]
   :post [(s/valid? ::bais %)]}
  (-> x
      byte-streams/to-byte-array
      byte-streams/to-input-stream))

;; -----------------------------------------------------------------------------
;; Walk map

(defn walk-map
  "Recursively apply a function to all map entries. When map is nil returns an
  empty map."
  [f m]
  (if m
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)
    {}))

(s/fdef walk-map
  :args (s/cat :f (s/fspec
                   :args (s/cat :kv (s/tuple any? any?))
                   :ret ::kv)
               :m ::maybe-any-map)
  :ret ::any-map)

;; -----------------------------------------------------------------------------
;; Inflections

(s/fdef inflectable?
  :args (s/cat :x any?)
  :ret boolean?)

(defn inflectable?
  [x]
  (or (keyword? x)
      (string? x)
      (symbol? x)))

(defn- inflect
  [x f]
  (cond-> x (inflectable? x) f))

(s/fdef string->keyword
  :args (s/cat :s string?)
  :ret keyword?)

(defn string->keyword
  [s]
  (-> s infl/hyphenate keyword))

(s/fdef keyword->string
  :args (s/cat :x any?)
  :fn (fn [{:keys [args ret]}]
        (if (-> args :x keyword?)
          (string? ret)
          (= (:x args) ret)))
  :ret any?)

(defn keyword->string
  [x]
  (if (keyword? x)
    (->> [(namespace x) (name x)]
         (remove nil?)
         (str/join "/"))
    x))

(s/fdef underscore-keys
  :args (s/cat :m ::maybe-any-map)
  :ret ::any-map)

(defn underscore-keys
  "Recursively transforms all map keys from hyphenated keywords, to underscored
  strings."
  [m]
  (walk-map
   (fn [[k v]] [(inflect k (comp infl/underscore keyword->string)) v]) m))

(s/fdef hyphenated-keyword
  :args (s/cat :x any?)
  :fn (fn [{:keys [args ret]}]
        (if (or (-> args :x keyword?) (-> args :x string?))
          (keyword? ret)
          (= (:x args) ret)))
  :ret any?)

(defn- hyphenated-keyword
  [x]
  (if (or (string? x) (keyword? x))
    (-> x keyword->string infl/hyphenate keyword)
    x))

(s/fdef hyphenate-keys
  :args (s/cat :m ::maybe-any-map)
  :ret ::any-map)

(defn hyphenate-keys
  "Recursively transforms all map keys from underscored strings to hyphenated
  keywords."
  [m]
  (walk-map (fn [[k v]] [(inflect k hyphenated-keyword) v]) m))
