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
(def slug "leap")
(def in-dir "/home/bob/clojure-test-runner/tests/example-success/")

(def slug (first *command-line-args*))
(def in-dir (second *command-line-args*))

(def test-ns (symbol (str slug "-test")))
(cp/add-classpath (str in-dir "src:" in-dir "test"))
(require test-ns)

;; clojure.test runs the tests in random order,
;; but the spec requires that we report them in order.

;; Parse test file into zipper using rewrite-clj
(def zloc (z/of-file (str in-dir "/test/" (str/replace slug "-" "_") "_test.clj")))

(def zloc-src (z/of-file (str in-dir "/src/" (str/replace slug "-" "_") ".clj")))

(defn test? 
  "Returns true if the given node is a `deftest`."
  [loc]
  (= (symbol 'deftest) (-> loc z/down z/sexpr)))

(defn test-name 
  "Returns the name of the test at a given node."
  [loc]
  (-> loc z/down z/right z/sexpr))

(defn fn-name 
  "Takes a zipper at a deftest node and outputs
   the name of the function being called."
  [loc]
    (cond
      (list? (z/sexpr loc)) (recur (z/down loc))
      (str/starts-with? (z/sexpr loc) slug) 
      (str/replace (str (z/sexpr loc)) (str slug "/") "")
      :else (recur (z/right loc))))
  
(defn top-level-forms 
  "Traverses a zipper from the root node
   and returns a sequence of the top-level forms"
  [z]
  (loop [loc z forms []]
    (cond
      (nil? loc) forms
      :else (recur (z/right loc) (conj forms loc)))))

(defn test-code?
  "Takes a top-level form node and a deftest node,
   and returns true if the function being called 
   matches the function called in the test."
  [form-loc test-loc]
  (= (fn-name test-loc)
     (str (-> form-loc z/down z/right z/sexpr))))

(comment
  (def node1
    (-> zloc z/right z/right z/right z/right z/right z/right z/right z/right))
  (def node2
    (-> zloc z/right z/right z/right z/right z/right z/right z/right z/right z/right))
  (z/sexpr node1)
   (z/sexpr node2)
  (str (-> node1 z/down z/right z/sexpr))
  (str (-> node2 z/down z/right z/sexpr))
  (fn-name node1)
  (fn-name node2)
  (test-code? (nth (top-level-forms zloc-src) 1) node1)
  (test-code? (nth (top-level-forms zloc-src) 1) node2)
  )

(defn test-code 
  "Takes a zipper at a deftest node,
   traverses the source file and returns the function being tested."
  [test-loc]
  (z/sexpr
   (first (filter #(test-code? % (-> test-loc))
                  (top-level-forms zloc-src)))))

(defn tests 
  "Traverses a zipper representing a parsed test file.
   Returns a vector of the test names in the order defined."
  [z]
  (loop [loc z tests []]
    (cond
      (nil? loc) tests
      (test? loc) (recur (z/right loc) (conj tests (test-name loc)))
      :else (recur (z/right loc) tests))))

(defn test-codes
  "Traverses a zipper representing a parsed test file.
   Returns a vector of the test codes in the order defined."
  [z]
  (loop [loc z tests []]
    (cond
      (nil? loc) tests
      (test? loc) (recur (z/right loc) (conj tests (test-code loc)))
      :else (recur (z/right loc) tests))))

(defn test-code-map [loc]
  (zipmap (tests loc) (test-codes loc)))

;; State to hold test results
(def passes (atom []))
(def fails (atom []))
(def errors (atom []))

;; Override clojure.test reporting methods to capture their results

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

;; Produce JSON output

(println (json/generate-string
      {:version 2
       :status (if (and (empty? @fails)
                        (empty? @errors))
                 "pass" "fail")
       :tests (vec (for [test (tests zloc)]
                     (cond
                       (contains? (set (map :name @passes)) test)
                       {:name test :status "pass" :test_code (str (test (test-code-map zloc)))}
                       (contains? (set (map :name @fails)) test)
                       {:name test :status "fail" :test_code (str (test (test-code-map zloc)))
                        :message (:message (first (filter #(= test (:name %)) @fails)))}
                       (contains? (set (set @errors)) test)
                       {:name test :status "error" :test_code (str (test (test-code-map zloc)))
                        :message (:message (first (filter #(= test (:name %)) @errors)))})))}
      {:pretty true}))

(System/exit 0)
