(ns leap-test
  (:require [clojure.test :refer [deftest testing is]]
            leap))

(deftest leap-year?
  (testing "year divisible by 4 and 5 is still a leap year"
    (is (leap/leap-year? 1960)))

  (testing "year not divisible by 4 in common year"
    (is (not (leap/leap-year? 2015))))

  (testing "year divisible by 400 is leap year"
    (is (leap/leap-year? 1970)))

  (testing "year divisible by 200, not divisible by 400 in common year"
    (is (not (leap/leap-year? 1800)))))
