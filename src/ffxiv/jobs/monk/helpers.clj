(ns ffxiv.jobs.monk.helpers
  (:require [clojure.spec.alpha :as s]
            [ffxiv.jobs.monk.specs :as monk-specs]
            [ffxiv.jobs.specs :as core-specs]))

(s/fdef calculate-multiplier
  :args (s/cat :config ::monk-specs/config)
  :ret double?)
(defn calculate-multiplier [config]
  (let [gl-mult (+ 1 (* 0.1 (-> config :job-buffs :gl)))
        stance-mult (if (= :fire (-> config :job-buffs :stance)) 1.1 1)
        twin-mult (if (< 0 (-> config :timers :durations :twin)) 1.1 1)
        rof-mult (if (< 0 (-> config :timers :durations :rof)) 1.25 1)
        bh-mult (if (< 0 (-> config :timers :durations :bh)) 1.05 1)]
    (reduce * 1 [gl-mult stance-mult twin-mult rof-mult bh-mult])))

(s/fdef calculate-potency
  :args (s/cat :config ::monk-specs/config
               :skill ::core-specs/skill)
  :ret double?)
(defn calculate-potency [config skill]
  (let [buff-mult (calculate-multiplier config)
        other-mult (reduce * 1 (map #(% config) (:potency-multipliers skill)))
        base-skill-potency-additives (reduce + 0 (map #(% config) (:potency-additives skill)))
        base-skill-potency (+ base-skill-potency-additives (:potency skill))]
    (* buff-mult base-skill-potency other-mult)))

(s/fdef calculate-gcd
  :args (s/cat :config ::monk-specs/config
               :skill ::core-specs/skill)
  :ret (s/or :zero zero? :double double?))
(defn calculate-gcd [config skill]
  (let [base-gcd 2.4
        gl-mult (- 1 (* 0.05 (-> config :job-buffs :gl)))
        new-gcd (* gl-mult base-gcd)]
    (if (not= :gcd (:type skill)) 0
        (-> new-gcd
            (* 100)
            (Math/floor)
            (/ 100)))))
