#!/usr/bin/env bb

(require '[clojure.test :as t]
         '[babashka.classpath :as cp]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[rewrite-clj.zip :as z])

(cp/add-classpath "libs.jar")

(require '[clojure.spec.alpha :as s]
         '[clojure.spec.gen.alpha :as gen])

;; Add solution source and tests to classpath
(def slug (first *command-line-args*))
(def in-dir (second *command-line-args*))
(def test-ns (symbol (str slug "-test")))
(cp/add-classpath (str in-dir "src:" in-dir "test"))
(require test-ns)

;; clojure.test runs the tests in random order,
;; but the spec requires that we report them in order.

;; Parse test file into zipper using rewrite-clj
(def zloc (z/of-file (str in-dir "/test/" (str/replace slug "-" "_") "_test.clj")))

(defn test?
  "Returns true if the given node is a `deftest`."
  [loc]
  (= (symbol 'deftest) (-> loc z/down z/sexpr)))

(defn assertion?
  "Returns true if the given node is an `is`."
  [loc]
  (= (symbol 'is) (-> loc z/down z/sexpr)))

(defn test-name
  "Returns the name of the test at a given node."
  [loc]
  (-> loc z/down z/right z/sexpr))

(defn test-code
  "Returns all of the test code for a given test"
  [loc]
  (->> (z/child-sexprs loc)
       (drop 2)
       (str/join "\n")))

(defn find-assertions
  "Returns a vector of the code for individual assertions in a given test."
  [loc]
  (->> (z/of-string (z/->string loc)) ;; isolate subtree to prevent find from leaving (is there a better way?)
       (iterate #(z/find-next-depth-first % assertion?))
       rest
       (take-while some?)
       (map #(->> (iterate z/up %)
                  (take-while (complement test?))
                  last))
       (map z/sexpr)
       (mapv str)))

(defn for-each-test
  "Traverses the zipper representing the parsed test file.
   Returns the result of calling f on each test."
  [f]
  (->> (iterate z/right zloc)
       (take-while some?)
       (filter test?)
       (map f)))

(def tests
  "A vector of the test names in the order defined."
  (for-each-test test-name))

(def test-codes
  "A vector of the test codes in the order defined."
  (for-each-test test-code))

(def test-assertions
  "A vector of vectors of test assertions in the order defined."
  (for-each-test find-assertions))

(def test-code-map (zipmap tests test-codes))
(def test-assertions-map (zipmap tests test-assertions))

;; State to hold test results
(def passes (atom []))
(def fails+errors (atom []))
(def assertion-counts (atom {})) ;; tracks which assertion is being reported on

;; logic for creating test results

(defn get-message [m status]
  (case status
    "fail" (str "Expected " (:expected m) " but got " (:actual m))
    "error" (str "An unexpected error occurred:\n" (:actual m))
    "pass" nil))

(defn get-test-code [test-name status]
  (let [a-count ((swap! assertion-counts update test-name (fnil inc 0)) test-name)]
    (if (= "pass" status)
      ;; for passing tests show the whole test
      (test-code-map test-name)
      ;; for fails just the one that failed
      ((test-assertions-map test-name) (dec a-count)))))

(defn report [m status]
  (let [name (:name (meta (first t/*testing-vars*)))
        message (get-message m status)]
    (merge {:name name :status status :test_code (get-test-code name status)}
           (when message {:message message}))))

;; Override clojure.test reporting methods to capture their results

(defmethod t/report :begin-test-ns [_])

(defmethod t/report :pass [m]
  (swap! passes conj (report m "pass")))

(defmethod t/report :fail [m]
  (swap! fails+errors conj (report m "fail")))

(defmethod t/report :error [m]
  (swap! fails+errors conj (report m "error")))

(defmethod t/report :summary [_])

(t/run-tests test-ns)

;; Produce JSON output

(println (json/generate-string
          {:version 2
           :status (if (empty? @fails+errors) "pass" "fail")
           :tests (for [test tests]
                    (->> (concat @fails+errors @passes) ;; failure takes priority!
                         (filter #(= test (:name %)))
                         first))}))

(System/exit 0)
