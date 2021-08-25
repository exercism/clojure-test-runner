#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp]
         '[cheshire.core :as json]
         '[clojure.string :as str])

(def test-ns (symbol (str (first *command-line-args*) "-test")))
(def in-dir (second *command-line-args*))

(cp/add-classpath "src:test")
(require test-ns)

(def passes (atom []))
(def fails (atom []))
(def errors (atom []))

(defmethod t/report :begin-test-ns [m])

(defmethod t/report :pass [m]
  (swap! passes conj {:name (:name (meta (first t/*testing-vars*)))
                      :status "pass"}))

(defmethod t/report :fail [m]
  (swap! fails conj {:name (:name (meta (first t/*testing-vars*)))
                     :status "fail"
                     :message (str "Expected " (:expected m) " but got " (:actual m))}))

(defmethod t/report :error [m]
  (swap! errors conj (:name (meta (first t/*testing-vars*)))))

(defmethod t/report :summary [m])

(t/run-tests test-ns)

(println (json/generate-string
      {:version 2
       :status (if (and (empty? @fails)
                        (empty? @errors))
                 "pass" "fail")
       :tests (into @passes @fails)
       :message (when (seq @errors)
                  @errors)}
      {:pretty true}))

(System/exit 0)