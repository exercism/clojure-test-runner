(ns leap-test
  (:require [clojure.test :refer [deftest is]]
            leap))

(deftest pass-fail-error
  (is (= true true) :1)
  (is (= true false) :2)
  (is (assert false) :3))

(deftest fail-error-pass
  (is (= true false) :1)
  (is (assert false) :2)
  (is (= true true) :3))

(deftest error-pass-fail
  (is (assert false) :1)
  (is (= true true) :2)
  (is (= true false) :3))

(deftest pass-fail-pass
  (is (= true true) :1)
  (is (= true false) :2)
  (is (= true true) :3))

(deftest pass-pass-pass
  (is (= true true) :1)
  (is (= true true) :2)
  (is (= true true) :3))

(deftest error-pass-error
  (is (assert false) :1)
  (is true :2)
  (is (assert false) :3))

(deftest pass-error-pass-error
  (is (not (leap/leap-year? 1900)))
  (is (leap/leap-year? 1960))
  (is (not (leap/leap-year? 1970)))
  (is (leap/leap-year? 1996)))

(deftest fail-pass
  (is (leap/leap-year? 2000))
  (is (not (leap/leap-year? 2015))))

(deftest pass-pass
  (is (not (leap/leap-year? 2100)))
  (is (not (leap/leap-year? 1800))))
