(ns ffxiv.jobs.monk.helpers-test
  (:require [clojure.test :refer [deftest testing is]]
            [ffxiv.jobs.monk.helpers :as helpers]
            [ffxiv.jobs.monk.specs :as monk-specs]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]
            [clojure.spec.gen.alpha :as gen]))

(def mockConfig
  (gen/generate (s/gen ::monk-specs/config)))

(set! s/*explain-out* expound/printer)

(deftest monk-helpers.fdefs
  (testing "Property tests for spec fdefs"
    (is (every? true?
                (-> (stest/enumerate-namespace `ffxiv.jobs.monk.helpers)
                    (stest/check {:clojure.spec.test.check/opts {:num-tests 10}})
                    #_expound/explain-results
                    :clojure.spec.test.check/ret
                    :pass?)))))

(deftest monk-helpers.calculate-gcd
  (testing "By deafult, base-gcd is multiplied by .80, and floored"
    (is (= 1.92 (helpers/calculate-gcd mockConfig {:type :gcd}))))

  (testing "When not a gcd, gcd is 0, because it's not on that 'rail'"
    (is (= 0 (helpers/calculate-gcd mockConfig {:type :ogcd})))))

(deftest monk-helpers.calculate-multiplier
  (testing "with no buffs, returns 1.0"
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :wind)
                         (assoc-in [:timers :durations :twin] 0)
                         (assoc-in [:timers :durations :rof] 0)
                         (assoc-in [:timers :durations :bh] 0))]
      (is (= 1.0 (helpers/calculate-multiplier monkConfig)))))

  (testing "with all buffs, returns "
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :fire)
                         (assoc-in [:timers :durations :twin] 1)
                         (assoc-in [:timers :durations :rof] 1)
                         (assoc-in [:timers :durations :bh] 1))]
      (is (= (* 1.1 1.1 1.25 1.05) (helpers/calculate-multiplier monkConfig))))))

(deftest monk-helpers.calculate-potency
  (testing "with no additives, when no buffs or other multipliers, returns base potency"
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :wind)
                         (assoc-in [:timers :durations :twin] 0)
                         (assoc-in [:timers :durations :rof] 0)
                         (assoc-in [:timers :durations :bh] 0))]
      (is (= 100.0
             (helpers/calculate-potency monkConfig {:potency 100.0})))))

  (testing "with no additives, but no multipliers, returns base potency + 100"
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :wind)
                         (assoc-in [:timers :durations :twin] 0)
                         (assoc-in [:timers :durations :rof] 0)
                         (assoc-in [:timers :durations :bh] 0))]
      (is (= 200.0
             (helpers/calculate-potency monkConfig {:potency 100.0
                                                    :potency-additives [(fn [_] 100)]})))))

  (testing "with no additives, but other multipliers, returns base potency * 2"
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :wind)
                         (assoc-in [:timers :durations :twin] 0)
                         (assoc-in [:timers :durations :rof] 0)
                         (assoc-in [:timers :durations :bh] 0))]
      (is (= 200.0
             (helpers/calculate-potency monkConfig {:potency 100.0
                                                    :potency-multipliers [(fn [_] 2)]})))))

  (testing "with no additives, or other multiplers, and all buffs, returns appropriate buffed potency"
    (let [monkConfig (-> mockConfig
                         (assoc-in [:job-buffs :stance] :fire)
                         (assoc-in [:timers :durations :twin] 1.0)
                         (assoc-in [:timers :durations :rof] 1.0)
                         (assoc-in [:timers :durations :bh] 1.0))]
      (is (= 158.0
             (Math/floor (helpers/calculate-potency monkConfig {:potency 100.0})))))))
