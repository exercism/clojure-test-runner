(ns exercism.clojure-test-runner
  (:require
   [cheshire.core :as json]
   [kaocha.repl :as k]
   [clojure.string :as str])
  (:gen-class))

(defn -main [slug in-dir out-dir]
  (let [test-file (load-file (str in-dir (str/replace slug "-" "_") "_test.clj"))
        ns (ns-publics (symbol (str slug "-test")))
        tests (keys ns)]
    (spit (str out-dir "results.json")
        (json/generate-string 
         (reduce (fn [m k]
              (assoc m k (pos? (:kaocha.result/pass (k/run (get ns k))))))
            {} tests)))))

(comment
  (-main "two-fer" "test/" "out-dir/"))