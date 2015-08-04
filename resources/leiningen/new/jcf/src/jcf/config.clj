(ns {{ns}}.config
  (:require [environ.core :refer [env]]
            [schema.core :as s]
            [schema.coerce :as coerce]))

(def ^:private defaults
  {:port 3000})

(def ^:private Config
  {:port s/Int})

(s/defn config-map :- Config
  ([] (config-map env))
  ([m]
   ((coerce/coercer Config coerce/string-coercion-matcher)
    (merge defaults (select-keys m (keys Config))))))
