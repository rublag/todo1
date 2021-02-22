(ns todo1.router-test
  (:require [todo1.router :refer :all]
            [clojure.test :refer :all]))

(deftest uri-slitting
  (testing "split /"
    (is (= (#'todo1.router/split-uri "/")
           [])))
  (testing "split /a/b/c"
    (is (= (#'todo1.router/split-uri "/a/b/c")
           ["a" "b" "c"]))))

(deftest is-string-a-keyword-pattern?
  (testing ":a"
    (is (= (#'todo1.router/keyword-string? ":a")
           true)))
  (testing "a"
    (is (= (#'todo1.router/keyword-string? "a")
          false))))

(deftest pattern-part-parsing
  (testing ":a"
    (is (= (#'todo1.router/parse-uri-pattern-part ":a")
           :a)))
  (testing "a"
    (is (= (#'todo1.router/parse-uri-pattern-part "a")
           "a"))))

(deftest uri-pattern-parsing
  (testing "/a/:b/c"
    (is (= (#'todo1.router/parse-uri-pattern "/a/:b/c")
           ["a" :b "c"]))))

(deftest part-matching
  (testing "a against a"
    (is (= (#'todo1.router/match-part "a" "a")
           true)))
  (testing "a against b"
    (is (= (#'todo1.router/match-part "a" "b")
           false)))
  (testing "a agains :x"
    (is (= (#'todo1.router/match-part "a" :x)
           {:x "a"}))))

(deftest uri-matching
  (testing "/a/b/c/d/e against /a/:x/c/:y/e"
    (is (= (#'todo1.router/match-pattern "/a/b/c/d/e" "/a/:x/c/:y/e")
           {:x "b" :y "d"})))
  (testing "a/b/d/c/e against /:x/:y/c/:z/e"
    (is (= (#'todo1.router/match-pattern "/a/b/d/c/e" ":x/:y/c/:z/e")
           nil))))
