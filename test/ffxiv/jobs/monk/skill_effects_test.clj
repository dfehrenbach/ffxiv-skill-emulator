(ns ffxiv.jobs.monk.helpers-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]))

(set! s/*explain-out* expound/printer)

(deftest gen-monk-effects
  (testing "Property tests for skill-effect fdefs"
    (is (every? true?
                (-> (stest/enumerate-namespace `ffxiv.jobs.monk.skill-effects)
                    (stest/check {:clojure.spec.test.check/opts {:num-tests 10}})
                    expound/explain-results)))))
