(ns exercism.clojure-test-runner-test
  (:require [clojure.test :refer :all]
            [exercism.clojure-test-runner :refer :all]))

(load-file "test/exercism/two-fer/src/example.clj")
(load-file "test/exercism/two-fer/test/two_fer_test.clj")

(run-tests 'two-fer-test)