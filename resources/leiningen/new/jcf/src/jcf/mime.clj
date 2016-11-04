(ns {{ns}}.mime
  (:require [clojure.string :as str]
            [medley.core :as medley]))

(defn parse-mime
  [s]
  (let [[ctype crest] (str/split s #"/" 2)
        [subtype & raw-params] (str/split crest #"\s*;\s*")
        params (->> raw-params
                    (map #(str/split % #"=" 2))
                    (into {})
                    (medley/map-keys str/lower-case))]
    {:type ctype
     :encoding (if (str/includes? subtype "+")
                 (last (str/split subtype #"\+" 2))
                 subtype)
     :subtype subtype
     :charset (get params "charset")
     :params params}))
