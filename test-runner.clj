#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp]
         '[cheshire.core :as json])

(cp/add-classpath "src:test")                        
(require (symbol (str (first *command-line-args*) "-test")))                  

(def passes (atom []))
(def fails (atom []))

(defmethod t/report :pass [m]
  (swap! passes conj (first t/*testing-vars*)))

(defmethod t/report :fail [m]
  (swap! fails conj (first t/*testing-vars*)))

(t/run-tests (symbol (str (first *command-line-args*) "-test")))

(spit (str (last *command-line-args*) "results.json")
      (json/generate-string {:passes (str @passes)
                                  :fails (str @fails)}
                            {:pretty true}))

(println (str "Results written to " (str (last *command-line-args*) "results.json")))
