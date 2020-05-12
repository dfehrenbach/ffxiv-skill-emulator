(ns mmo-button.calculate-test
  (:require [clojure.test :refer [deftest testing is]]
            [mmo-button.calculate :as calc]))

(deftest calculate.update-timer
  (testing "reduces a timer in a config from 10 to 5"
    (let [mockConfig {:timers {:cooldowns {:some-cd 10}}}]
      (is (= (assoc-in mockConfig [:timers :cooldowns :some-cd] 5)
             ((calc/update-timer :some-cd [:timers :cooldowns] 5) mockConfig)))))

  (testing "reduces a timer in a config from 3 to -2"
    (let [mockConfig {:timers {:cooldowns {:some-cd 3}}}]
      (is (= (assoc-in mockConfig [:timers :cooldowns :some-cd] -2)
             ((calc/update-timer :some-cd [:timers :cooldowns] 5) mockConfig)))))

  (testing "resets a timer in a config from -2 to 0"
    (let [mockConfig {:timers {:cooldowns {:some-cd -2}}}]
      (is (= (assoc-in mockConfig [:timers :cooldowns :some-cd] 0)
             ((calc/update-timer :some-cd [:timers :cooldowns] 5) mockConfig))))))

(deftest calculate.update-dot
  (testing "updates a dot when able, [duration-remaining, ticks-remaining, can-tick]"
    (let [mockConfig {:dots {:durations {:some-dot 18}
                             :ticks {:some-dot 6}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}}
          result (-> mockConfig
                     (assoc-in [:dots :ticks :some-dot] 5)
                     (assoc-in [:total-potency] 100))]
      (is (= result
             ((calc/update-dot :some-dot 3 mockJob) mockConfig)))))

  (testing "does not a dot when there's no duration-remaining and ticks are 0"
    (let [mockConfig {:dots {:durations {:some-dot -1.0}
                             :ticks {:some-dot 0}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}}]
      (is (= mockConfig
             ((calc/update-dot :some-dot 3 mockJob) mockConfig)))))

  (testing "does not a dot when it's already ticked within this period"
    (let [mockConfig {:dots {:durations {:some-dot 17}
                             :ticks {:some-dot 5}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}}]
      (is (= mockConfig
             ((calc/update-dot :some-dot 3 mockJob) mockConfig))))))

(deftest calculate.tick-down
  (testing "doesn't tick down anything if there's nothing to tick down"
    (let [mockConfig {:timers {:cooldowns {:some-cd 0}
                               :durations {:some-cd 0}}
                      :dots {:durations {:some-dot 0}
                             :ticks {:some-dot 5}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}
                   :calculate-gcd (fn [_ _] 3)}
          mockSkill {:potency 100.0 :type :gcd}]
      (is (= mockConfig
             (calc/tick-down mockConfig mockJob mockSkill)))))

  (testing "ticks down a dot duration & tick (if necessary)"
    (let [mockConfig {:timers {:cooldowns {:some-cd 0}
                               :durations {:some-cd 0}}
                      :dots {:durations {:some-dot 18}
                             :ticks {:some-dot 6}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}
                   :calculate-gcd (fn [_ _] 3)}
          mockSkill {:potency 200.0 :type :gcd}
          result (-> mockConfig
                     (assoc-in [:dots :durations :some-dot] 15)
                     (assoc-in [:dots :ticks :some-dot] 5)
                     (assoc-in [:total-potency] 100))]
      (is (= result
             (calc/tick-down mockConfig mockJob mockSkill)))))

  (testing "ticks down a buffs cooldown and duration"
    (let [mockConfig {:timers {:cooldowns {:some-cd 9}
                               :durations {:some-cd 6}}
                      :dots {:durations {:some-dot 0}
                             :ticks {:some-dot 5}
                             :multipliers {:some-dot 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot {:tick-potency 100}}
                   :calculate-gcd (fn [_ _] 3)}
          mockSkill {:potency 200.0 :type :gcd}
          result (-> mockConfig
                     (assoc-in [:timers :cooldowns :some-cd] 6)
                     (assoc-in [:timers :durations :some-cd] 3))]
      (is (= result
             (calc/tick-down mockConfig mockJob mockSkill)))))

  (testing "ticks down buffs and dots"
    (let [mockConfig {:timers {:cooldowns {:some-cd1 9 :some-cd2 19}
                               :durations {:some-cd1 6 :some-cd2 16}}
                      :dots {:durations {:some-dot1 18 :some-dot2 12}
                             :ticks {:some-dot1 6 :some-dot2 4}
                             :multipliers {:some-dot1 1 :some-dot2 1}}
                      :total-potency 0}
          mockJob {:skill-map {:some-dot1 {:tick-potency 10}
                               :some-dot2 {:tick-potency 20}}
                   :calculate-gcd (fn [_ _] 3)}
          mockSkill {:potency 200.0 :type :gcd}
          result (-> mockConfig
                     (assoc-in [:timers :cooldowns :some-cd1] 6)
                     (assoc-in [:timers :durations :some-cd1] 3)
                     (assoc-in [:timers :cooldowns :some-cd2] 16)
                     (assoc-in [:timers :durations :some-cd2] 13)
                     (assoc-in [:dots :durations :some-dot1] 15)
                     (assoc-in [:dots :ticks :some-dot1] 5)
                     (assoc-in [:dots :durations :some-dot2] 9)
                     (assoc-in [:dots :ticks :some-dot2] 3)
                     (assoc-in [:total-potency] 30))]
      (is (= result
             (calc/tick-down mockConfig mockJob mockSkill))))))

(deftest calculate.skill-director
  (testing "with no restrictions, and no effects, a skill applied, potency added, and time increased"
    (let [mockConfig {:time-elapsed 0.0
                      :total-potency 0}
          mockSkill {:potency 100 :restrictions []}
          mockJob {:calculate-potency (fn [_ _] (:potency mockSkill))
                   :calculate-gcd (fn [_ _] 3)}
          result (-> mockConfig
                     (assoc-in [:time-elapsed] 3.0)
                     (assoc-in [:total-potency] 100))]
      (is (= result
             (calc/skill-director mockConfig mockJob mockSkill)))))

  ;; TODO:
  ;;; We could add an error list of some sort to keep track of what skills couldn't be applied
  (testing "with a failing restrictions skill is not applied"
    (let [mockConfig {:time-elapsed 0.0
                      :total-potency 0}
          mockSkill {:potency 100
                     :name "skill-director mocked skill"
                     :restrictions [(fn [_] false) (fn [_] true)]}
          mockJob {:calculate-potency (fn [_ _] (:potency mockSkill))
                   :calculate-gcd (fn [_ _] 3)}
          result (-> mockConfig
                     (assoc-in [:time-elapsed] 0.0)
                     (assoc-in [:total-potency] 0))]
      (is (= result
             (calc/skill-director mockConfig mockJob mockSkill)))))

  (testing "with a passing restriction skill is applied and effects are completed and timers tick down"
    (let [mockConfig {:time-elapsed 0.0
                      :total-potency 0
                      :job-buffs {:some-buff1 0
                                  :some-buff2 0}
                      :timers {:cooldowns {:some-buff 8.5}}}
          mockSkill {:potency 100
                     :restrictions [(fn [_] true) (fn [_] true)]
                     :effects [(fn [config] (assoc-in config [:job-buffs :some-buff1] 1))
                               (fn [config] (assoc-in config [:job-buffs :some-buff2] 2))]}
          mockJob {:calculate-potency (fn [_ _] (:potency mockSkill))
                   :calculate-gcd (fn [_ _] 3)}
          result (-> mockConfig
                     (assoc-in [:time-elapsed] 3.0)
                     (assoc-in [:total-potency] 100)
                     (assoc-in [:job-buffs :some-buff1] 1)
                     (assoc-in [:job-buffs :some-buff2] 2)
                     (assoc-in [:timers :cooldowns :some-buff] 5.5))]
      (is (= result
             (calc/skill-director mockConfig mockJob mockSkill))))))

(deftest calculate.rotation-director
  (testing "takes a set of valid skills and applies them all in order"
    (let [mockBaseConfig {:job :someJob}
          mockJob {:init {:time-elapsed 0.0
                          :total-potency 0}
                   :skill-map {:skill1 {:potency 100}
                               :skill2 {:potency 10}}
                   :calculate-potency (fn [_ skill] (:potency skill))
                   :calculate-gcd (fn [_ _] 3)}
          mockSkillsArr [:skill1 :skill2 :skill2]
          result (-> (merge mockBaseConfig (mockJob :init))
                     (assoc-in [:time-elapsed] 9.0)
                     (assoc-in [:total-potency] 120))]
      (is (= result
             (calc/rotation-director mockBaseConfig mockJob mockSkillsArr)))))

  (testing "takes a mixed set of valid and invalid skills and applies only valid skills"
    (let [mockBaseConfig {:job :someJob}
          mockJob {:init {:time-elapsed 0.0
                          :total-potency 0}
                   :skill-map {:skill1 {:potency 100
                                        :restrictions [(fn [_] false)]
                                        :name "rotation-director mocked skill"}
                               :skill2 {:potency 10}}
                   :calculate-potency (fn [_ skill] (:potency skill))
                   :calculate-gcd (fn [_ _] 3)}
          mockSkillsArr [:skill1 :skill2 :skill2]
          result (-> (merge mockBaseConfig (mockJob :init))
                     (assoc-in [:time-elapsed] 6.0)
                     (assoc-in [:total-potency] 20))]
      (is (= result
             (calc/rotation-director mockBaseConfig mockJob mockSkillsArr))))))
