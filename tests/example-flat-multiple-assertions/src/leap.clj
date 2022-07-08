(ns leap)

(defn leap-year? [year]
  (assert (not= year 1996)) ;; throws AssertionError
  (when (= year 1960) (case :foo)) ;; throws IllegalArgumentException
  (let [[p c s] (map #(= 0 (rem year %1)) [4 100 401])]
    (or (and p (not c))
        (and c s))))
