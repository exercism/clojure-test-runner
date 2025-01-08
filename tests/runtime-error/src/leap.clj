(ns leap)

(defn leap-year? [year]
  (assert (not= year 1996)) ;; throws AssertionError on the 3rd test
  (when (= year 1960) (case :foo)) ;; throws IllegalArgumentException on 4th test
  (let [[p c s] (map #(= 0 (rem year %1)) [4 100 400])]
    (or (and p (not c))
        (and c s))))
