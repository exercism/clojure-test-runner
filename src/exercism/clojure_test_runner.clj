(ns exercism.clojure-test-runner
  (:require
   [clojure.test :refer [deftest testing is run-tests]]
   [cheshire.core :as json]
   [kaocha.repl :as k])
  (:gen-class))

(defn test-slug [slug]
  (let [ns (ns-publics (symbol (str slug "-test")))
        tests (keys ns)]
    (reduce (fn [m k]
              (assoc m k (pos? (:kaocha.result/pass (k/run (get ns k))))))
            {} tests)))

(defn -main
  "Tests a solution file and writes `results.json` to output directory"
  [slug input output]
  (spit (str output "results.json")
        (json/generate-string {:slug slug :input input :output output})))

(comment
  (json/generate-string
   (test-slug "two-fer"))
  (-main "two-fer" "in-dir/" "out-dir/"))