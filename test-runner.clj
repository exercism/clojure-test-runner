#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[rewrite-clj.zip :as z])

(def slug (first *command-line-args*))
(def in-dir (second *command-line-args*))
(def test-ns (symbol (str slug "-test")))

(cp/add-classpath (str in-dir "src:" in-dir "test"))

(def zloc (z/of-file (str in-dir "/test/" (str/replace slug "-" "_") "_test.clj")))

(defn test? [loc]
  (= (symbol 'deftest) (-> loc z/down z/sexpr)))

(defn test-name [loc]
  (-> loc z/down z/right z/sexpr))

(defn tests [z]
  (loop [loc z
         tests []]
    (cond
      (nil? loc) tests
      (test? loc) (recur (z/right loc) (conj tests (test-name loc)))
      :else (recur (z/right loc) tests))))

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
       :tests (vec (for [test (tests zloc)]
                     (cond
                       (contains? (set (map :name @passes)) test)
                       {:name test :status "pass"}
                       (contains? (set (map :name @fails)) test)
                       {:name test :status "fail"})))
       :message (when (seq @errors)
                  @errors)}
      {:pretty true}))

(System/exit 0)