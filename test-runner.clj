#!/usr/bin/env bb

(require '[babashka.classpath :as cp]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[rewrite-clj.zip :as z])

(def slug "annalyns-infiltration")
(def slug "lucians-luscious-lasagna")
(def in-dir "/home/porky/exercism/clojure-test-runner/exercises/concept/annalyns-infiltration/")
(def in-dir "/home/porky/exercism/clojure-test-runner/exercises/concept/lucians-luscious-lasagna/")
(load-file "/home/porky/exercism/clojure-test-runner/exercises/concept/annalyns-infiltration/src/annalyns_infiltration.clj")
;; Add solution source and tests to classpath
(def slug (first *command-line-args*))
(def in-dir (second *command-line-args*))
(def test-ns (symbol (str slug "-test")))
(cp/add-classpath (str in-dir "src:" in-dir "test"))
(require test-ns)
(require (symbol slug))

;; Parse test file into zipper using rewrite-clj
(def zloc (z/of-file (str in-dir "/test/" (str/replace slug "-" "_") "_test.clj")))

(defn deftest? 
  "Returns true if the given node is a `deftest`."
  [loc]
  (= (symbol 'deftest) (-> loc z/down z/sexpr)))

(defn testing?
  "Returns true if the given node is a `testing` form."
  [loc]
  (= (symbol 'testing) (-> loc z/down z/sexpr)))

(defn assertion?
  "Returns true if the given node is an `is` form."
  [loc]
  (= (symbol 'is) (-> loc z/down z/sexpr)))

(defn assertion-next?
  "Returns true if the node to the right is an `is` form."
  [loc]
  (= (symbol 'is) (-> loc z/right z/down z/sexpr)))

(defn outer-testing?
  "Returns true if the given node is an outer `testing` form."
  [loc]
  (and
   (= (symbol 'testing) (-> loc z/down z/sexpr))
   (= (symbol 'testing) (-> loc z/down z/right z/right z/down z/sexpr))))

(defn assertion-true? 
  "Returns true if the given assertion-loc is true."
  [loc]
  (eval (-> loc z/sexpr)))

(defn test-deftest [loc]
  (let [deftest loc]
    (loop [loc deftest prefix-string ""
           test-strings [] results [] assertions []]
      (cond
        (deftest? loc)
        (recur (-> loc z/down z/right z/right)
               prefix-string test-strings results assertions)
        (outer-testing? loc)
        (recur (-> loc z/down z/right z/right)
               (-> loc z/down z/right z/sexpr)
               test-strings results assertions)
        (testing? loc)
        (recur (-> loc z/down z/right z/right)
               prefix-string
               (conj test-strings
                     (str/trim (str prefix-string " "
                                    (-> loc z/down z/right z/sexpr))))
               (conj results [])
               assertions)
        (and (assertion? loc) (assertion-next? loc))
        (recur (-> loc z/right)
               prefix-string
               test-strings
               (conj results [(assertion-true? loc)])
               (conj assertions (z/sexpr loc)))
        (assertion? loc)
        (recur (-> loc z/up z/right)
               prefix-string
               test-strings
               (conj (vec (butlast results))
                     (conj (vec (last results)) (assertion-true? loc)))
               (conj assertions (z/sexpr loc)))
        :else
        {:test-name (-> deftest z/down z/right z/sexpr str)
         :results (vec (remove empty? results))
         :test-strings test-strings
         :assertions assertions}))))

(defn test-file [loc]
  (loop [loc loc
         tests []]
    (cond
      (nil? loc) tests
      (deftest? loc) (recur (-> loc z/right) (conj tests (test-deftest loc)))
      :else (recur (-> loc z/right) tests))))

(comment
  (test-file zloc)
  )

(defn results [loc]
  (flatten
   (for [test (test-file zloc)]
     (if (empty? (:test-strings test))
       {:name (:test-name test)
        :status (if (every? true? (:results test))
                  "pass" "fail")}
       (for [n (range (count (:test-strings test)))]
         {:name (get (:test-strings test) n)
          :status (if (every? true? (get (:results test) n))
                    "pass" "fail")
          :test_code (get (:assertions test) n)})))))

(comment
  (results zloc)
  )

;; Produce JSON output

(println (json/generate-string
      {:version 2
       :status (if (every? #(= "pass" (:status %)) (results zloc))
                 "pass" "fail")
       :tests
       (vec (results zloc))}
      {:pretty true}))

(System/exit 0)