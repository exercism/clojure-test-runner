(ns leap)

(defn leap-year? [year]
  (let [[p c s] (map #(= 0 (rem year %1)) [4 100 400])]
    (not
      (or (and p (not c))
          (and c s)))))
    
