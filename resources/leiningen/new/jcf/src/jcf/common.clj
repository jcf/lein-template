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

(defn boolean?
  [x]
  (instance? Boolean x))

(s/def ::boolean
  (s/with-gen boolean? gen/boolean))

(s/def ::any-map
  (s/map-of (s/nilable ::s/any) (s/nilable ::s/any)))

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
  (s/tuple ::s/any ::s/any))

;; -----------------------------------------------------------------------------
;; ByteArrayInputStream

(defn bais
  [x]
  {:pre [(s/valid? ::baisable x)]
   :post [(s/valid? ::bais %)]}
  (-> x
      byte-streams/to-byte-array
      byte-streams/to-input-stream))

(s/fdef bais
  :args (s/cat :x ::baisable)
  :ret ::bais)

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
                   :args (s/cat :kv (s/tuple ::s/any ::s/any))
                   :ret ::kv)
               :m ::maybe-any-map)
  :ret ::any-map)

;; -----------------------------------------------------------------------------
;; Inflections

(defn inflectable?
  [x]
  (or (keyword? x)
      (string? x)
      (symbol? x)))

(s/fdef inflectable?
  :args (s/cat :x ::s/any)
  :ret ::boolean)

(defn- inflect
  [x f]
  (cond-> x (inflectable? x) f))

(defn string->keyword
  [s]
  (-> s infl/hyphenate keyword))

(s/fdef string->keyword
  :args (s/cat :s string?)
  :ret keyword?)

(defn keyword->string
  [x]
  (if (keyword? x)
    (->> [(namespace x) (name x)]
         (remove nil?)
         (str/join "/"))
    x))

(s/fdef keyword->string
  :args (s/cat :x ::s/any)
  :fn (fn [{:keys [args ret]}]
        (if (-> args :x keyword?)
          (string? ret)
          (= (:x args) ret)))
  :ret ::s/any)

(defn underscore-keys
  "Recursively transforms all map keys from hyphenated keywords, to underscored
  strings."
  [m]
  (walk-map
   (fn [[k v]] [(inflect k (comp infl/underscore keyword->string)) v]) m))

(s/fdef underscore-keys
  :args (s/cat :m ::maybe-any-map)
  :ret ::any-map)

(defn- hyphenated-keyword
  [x]
  (if (or (string? x) (keyword? x))
    (-> x keyword->string infl/hyphenate keyword)
    x))

(s/fdef hyphenated-keyword
  :args (s/cat :x ::s/any)
  :fn (fn [{:keys [args ret]}]
        (if (or (-> args :x keyword?) (-> args :x string?))
          (keyword? ret)
          (= (:x args) ret)))
  :ret ::s/any)

(defn hyphenate-keys
  "Recursively transforms all map keys from underscored strings to hyphenated
  keywords."
  [m]
  (walk-map (fn [[k v]] [(inflect k hyphenated-keyword) v]) m))

(s/fdef hyphenate-keys
  :args (s/cat :m ::maybe-any-map)
  :ret ::any-map)
