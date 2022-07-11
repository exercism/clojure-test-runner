(ns leap-test
  (:require [clojure.test :refer [deftest is testing]]
            leap))

;; below tests are adapted from examples of nested tests found in the clojure track
;; each has a case where all assertions pass, where one fails, and where one errors

(deftest day-test-pass
  (testing "numeric pattern for day matches"
    (testing "un-padded 1"
      (is (= "1" "1")))
    (testing "un-padded 2"
      (is (= "2" "2")))
    (testing "un-padded 3"
      (is (= "3" "3")))
    (testing "un-padded 4"
      (is (= "4" "4"))))
  (testing "numeric pattern for day doesn't match"
    (testing "too few digits"
      (is (nil? nil)))
    (testing "too many digits"
      (is (nil? nil)))
    (testing "one letter"
      (is (nil? nil)))
    (testing "two letters"
      (is (nil? nil)))))

(deftest day-test-fail
  (testing "numeric pattern for day matches"
    (testing "un-padded 1"
      (is (= "1" "1")))
    (testing "un-padded 2"
      (is (= "2" "2")))
    (testing "un-padded 3"
      (is (= "3" "WRONG")))
    (testing "un-padded 4"
      (is (= "4" "4"))))
  (testing "numeric pattern for day doesn't match"
    (testing "too few digits"
      (is (nil? nil)))
    (testing "too many digits"
      (is (nil? nil)))
    (testing "one letter"
      (is (nil? nil)))
    (testing "two letters"
      (is (nil? nil)))))

(deftest day-test-error
  (testing "numeric pattern for day matches"
    (testing "un-padded 1"
      (is (= "1" "1")))
    (testing "un-padded 2"
      (is (= "2" "2")))
    (testing "un-padded 3"
      (is (= "3" "3")))
    (testing "un-padded 4"
      (is (= "4" "4"))))
  (testing "numeric pattern for day doesn't match"
    (testing "too few digits"
      (is (nil? nil)))
    (testing "too many digits"
      (is (nil? nil)))
    (testing "one letter"
      (is (nil? (case :oh-no!))))
    (testing "two letters"
      (is (nil? nil)))))

(deftest day-names-test-pass
  (testing "day names match"
    (is (= "Sunday" "Sunday"))
    (is (= "Monday" "Monday"))
    (is (= "Tuesday" "Tuesday"))
    (is (= "Wednesday" "Wednesday"))
    (is (= "Thursday" "Thursday"))
    (is (= "Friday" "Friday"))
    (is (= "Saturday" "Saturday")))
  (testing "day names don't match"
    (testing "combined"
      (is (nil? nil)))
    (testing "short name"
      (is (nil? nil)))
    (testing "numeric day of the week (0-indexed)"
      (is (nil? nil)))
    (testing "numeric day of the week (1-indexed)"
      (is (nil? nil)))))

(deftest day-names-test-fail
  (testing "day names match"
    (is (= "Sunday" "Sunday"))
    (is (= "Monday" "Monday"))
    (is (= "Tuesday" "Tuesday"))
    (is (= "Wednesday" "Wednesday"))
    (is (= "Thursday" "Thursday"))
    (is (= "Friday" "Friday"))
    (is (= "Saturday" "Saturday")))
  (testing "day names don't match"
    (testing "combined"
      (is (nil? false)))
    (testing "short name"
      (is (nil? nil)))
    (testing "numeric day of the week (0-indexed)"
      (is (nil? nil)))
    (testing "numeric day of the week (1-indexed)"
      (is (nil? nil)))))

(deftest day-names-test-error
  (testing "day names match"
    (is (= "Sunday" "Sunday"))
    (is (= "Monday" "Monday"))
    (is (= "Tuesday" "Tuesday"))
    (is (= "Wednesday" "Wednesday"))
    (is (= "Thursday" "Thursday"))
    (is (= "Friday" "Friday"))
    (is (= "Saturday" (assert nil))))
  (testing "day names don't match"
    (testing "combined"
      (is (nil? nil)))
    (testing "short name"
      (is (nil? nil)))
    (testing "numeric day of the week (0-indexed)"
      (is (nil? nil)))
    (testing "numeric day of the week (1-indexed)"
      (is (nil? nil)))))

(deftest increment-decrement-and-get-balance-pass
  (testing "Taking money out of the account works"
    (let [account (atom 0)]
      (is (= 0 @account))
      (swap! account + 10)
      (is (= 10 @account))
      (swap! account + -10)
      (is (= 0 @account)))))

(deftest increment-decrement-and-get-balance-fail
  (testing "Taking money out of the account works"
    (let [account (atom 0)]
      (is (= 0 @account))
      (swap! account + 10)
      (is (= 1000 @account))
      (swap! account + -10)
      (is (= 0 @account)))))

(deftest increment-decrement-and-get-balance-error
  (testing "Taking money out of the account works"
    (let [account (atom 0)]
      (is (= 0 @account))
      (swap! account + 10)
      (is (= 10 @@account))
      (swap! account + -10)
      (is (= 0 @account)))))

(deftest largest-series-tests-pass
  (testing "can find the largest product of 2 with numbers in order"
    (is (= 72 72)))
  (testing "can find the largest product of 2"
    (is (= 48 48)))
  (testing "finds the largest product if span equals length"
    (is (= 18 18))))

(deftest largest-series-tests-fail
  (testing "can find the largest product of 2 with numbers in order"
    (is (= 72 "WRONG")))
  (testing "can find the largest product of 2"
    (is (= 48 48)))
  (testing "finds the largest product if span equals length"
    (is (= 18 18))))

(deftest largest-series-tests-error
  (testing "can find the largest product of 2 with numbers in order"
    (is (= 72 72)))
  (testing "can find the largest product of 2"
    (is (= 48 48)))
  (testing "finds the largest product if span equals length"
    (is (= 18 (+ :foo)))))

(deftest is-inside-let-pass
  (let [v [1 2 3 4]]
    (is (= 4 (count v)))
    (is (sequential? v))
    (is (= 2 (second v)))))

(deftest is-inside-let-fail
  (let [v (range 4)]
    (is (= 4 (count v)))
    (is (sequential? v))
    (is (= 2 (second v)))))

(deftest is-inside-let-error
  (let [v 4]
    (is (= 4 (count v)))
    (is (sequential? v))
    (is (= 2 (second v)))))
