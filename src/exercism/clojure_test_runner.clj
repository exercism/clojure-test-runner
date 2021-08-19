(ns exercism.clojure-test-runner
  (:require
   [clojure.test :refer [deftest testing is run-tests test-all-vars test-ns test-var]]
   [cheshire.core :as json]
   [kaocha.repl :as k]
   [clojure.java.shell :as shell]
   [clojure.string :as str])
  (:gen-class))

(defn -main [slug in-dir out-dir]
  (let [ns (ns-publics (symbol (str slug "-test")))
        tests (keys ns)]
    (shell/sh "cp" (str in-dir (str/replace slug "-" "_") ".clj") "src")
    (shell/sh "cp" (str in-dir (str/replace slug "-" "_") "_test.clj") "test")
    (spit (str out-dir "results.json")
        (json/generate-string 
         (reduce (fn [m k]
              (assoc m k (pos? (:kaocha.result/pass (k/run (get ns k))))))
            {} tests)))))

(comment
  (json/generate-string
   (test-slug "two-fer" "resources/" ""))
  (-main "two-fer" "resources/" "out-dir/"))