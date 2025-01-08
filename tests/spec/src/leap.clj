(ns leap
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]))

(defn leap-year? [year]
  (let [[p c s] (map #(= 0 (rem year %1)) [4 100 400])]
    (or (and p (not c))
        (and c s))))

(gen/sample (s/gen int?))

(s/def ::street (s/and string? (complement str/blank?)))
(s/def ::street-number int?)
(s/def ::address (s/keys :req-un [::street ::street-number]))
(s/def ::name (s/and string? (complement str/blank?)))
(s/def ::contact (s/keys :req-un [::name ::address]))
(gen/sample (s/gen ::contact))

