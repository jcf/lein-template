(ns {{ns}}.common-test
  (:require byte-streams
          [clojure.test :refer :all]
          [clojure.test.check
           [clojure-test :refer [defspec]]
           [generators :as gen]
           [properties :as prop]]
          [{{ns}}.common :as sut]))

;; -----------------------------------------------------------------------------
;; ByteArrayInputStream

(defspec t-bais
  1000
  (prop/for-all [s gen/string]
    (= s (byte-streams/to-string (sut/bais s)))))

;; -----------------------------------------------------------------------------
;; Inflections

(deftest t-walk-map
  (are [f m x] (= (sut/walk-map f m) x)
    identity nil    {}
    identity {}     {}
    identity {:a 1} {:a 1}

    (fn [[k ^long v]] [(name k) (if (number? v) (inc v) v)])
    {:a 1 :b {:c 2 :d {:e 3}}}
    {"a" 2 "b" {"c" 3 "d" {"e" 4}}}))

(deftest t-keyword->string
  (are [x y] (= (sut/keyword->string x) y)
    :var    "var"
    :ns/var "ns/var"
    :a-b    "a-b"
    :a_b    "a_b"
    "a-b/c" "a-b/c"))

(deftest t-string->keyword
  (are [x y] (= (sut/string->keyword x) y)
    "var"    :var
    "ns/var" :ns/var
    "a-b"    :a-b
    "a_b"    :a-b
    "a-b/c"  :a-b/c))

(deftest t-hyphenate-keys
  (are [x y] (= (sut/hyphenate-keys x) y)
    nil                    {}
    {}                     {}
    {nil "value"}          {nil "value"}
    {:a 1 "b" 2}           {:a 1 :b 2}
    {:a 1 "a" 2}           {:a 2}
    {:a 1 "a" 2}           {:a 2}
    {:a 1 "a-b" 2 "a_b" 3} {:a 1 :a-b 3}))

(deftest t-underscore-keys
  (are [x y] (= (sut/underscore-keys x) y)
    nil                    {}
    {}              {}
    {nil "value"}   {nil "value"}
    {:a 1 "b" 2}    {"a" 1 "b" 2}
    {:a-b 1 :a_b 2} {"a_b" 2}
    {"a-b" 1}       {"a_b" 1}))
