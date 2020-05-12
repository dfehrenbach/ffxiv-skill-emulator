(ns mmo-button.calculate
  (:require [mmo-button.jobs.monk.monk :refer [monk]]))

(defn update-timer [timer timer-path tick-time]
  (fn [conf]
    (let [full-path (conj timer-path timer)
          cur-timer (get-in conf full-path)]
      (assoc-in conf full-path
                (if (not (pos? cur-timer)) 0
                    (- cur-timer tick-time))))))

(defn update-dot [dot tick-time job]
  (fn [conf]
    (let [duration (get-in conf [:dots :durations dot])
          ticks (get-in conf [:dots :ticks dot])
          multiplier (get-in conf [:dots :multipliers dot])
          potency (get-in (job :skill-map) [dot :tick-potency])]
      (if (and
           (<= (- duration tick-time) (* 3 (- ticks 1)))
           (pos? ticks)
           (pos? duration))
        (-> conf
            (assoc-in [:dots :ticks dot] (- ticks 1))
            (update-in [:total-potency] + (* multiplier potency)))
        conf))))

;; TODO: Auto Attacks
;; TODO: Timed Skill Effects (Brotherhood)
(defn tick-down [config job skill]
  (let [tick-time ((job :calculate-gcd) config skill)
        update-timers (fn [timer-path]
                        (map
                         #(update-timer % timer-path tick-time)
                         (keys (get-in config timer-path))))
        update-dots (fn [dots-path]
                      (map
                       #(update-dot % tick-time job)
                       (keys (get-in config dots-path))))]
    ((comp
      (apply comp (update-dots [:dots :durations]))
      (apply comp (update-timers [:dots :durations]))
      (apply comp (update-timers [:timers :durations]))
      (apply comp (update-timers [:timers :cooldowns]))) config)))

(defn printutil [returnval & things-to-print]
  (println things-to-print)
  returnval)

(defn skill-director [config job skill]
  (let [allowed? (or (every? true? (map #(% config) (:restrictions skill))) (empty? (:restrictions skill)))
        initial-config (-> config
                           (update :total-potency + ((job :calculate-potency) config skill))
                           (update :time-elapsed + ((job :calculate-gcd) config skill)))
        all-skill-effects (apply comp (:effects skill))]
    (if allowed?
      (all-skill-effects (tick-down initial-config job skill))
      (printutil config "NOT APPLIED" (:name skill) "*********************************"))))

(defn rotation-director [base-config job skills-arr]
  (let [battle-config (merge base-config (job :init))
        mapped-skills (map (job :skill-map) skills-arr)]
    (reduce (fn [conf skill] (skill-director conf job skill))
            battle-config
            mapped-skills)))
