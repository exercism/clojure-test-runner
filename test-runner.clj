#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp]
         '[cheshire.core :as json]
         '[clojure.string :as str])

(def test-ns (symbol (str (first *command-line-args*) "-test")))
(def in-dir (second *command-line-args*))

(cp/add-classpath (str/replace in-dir "/" ""))                        
(require test-ns)                  

(def passes (atom []))
(def fails (atom []))

(defmethod t/report :pass [m]
  (swap! passes conj (:name (meta (first t/*testing-vars*)))))

(defmethod t/report :fail [m]
  (swap! fails conj (:name (meta (first t/*testing-vars*)))))

(t/run-tests test-ns)

(spit (str (last *command-line-args*) "results.json")
      (json/generate-string {:version 2
                             :status (if (empty? @fails)
                                       "pass" "fail")
                             :passes @passes
                                  :fails @fails}
                            {:pretty true}))

(println (str "Results written to " (str (last *command-line-args*) "results.json")))
(System/exit 0)