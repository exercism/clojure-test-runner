(ns exercism.clojure-test-runner
  (:require
   [clojure.test :refer [deftest testing is]]
   [cheshire.core :as json])
  (:gen-class))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment
  (json/generate-string {:foo "bar" :baz 5}))