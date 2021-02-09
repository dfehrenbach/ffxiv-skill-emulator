(ns ffxiv.calculate
  (:require [ffxiv.jobs.monk.monk :refer [monk]]
            [postmortem.core :as pm]
            [postmortem.instrument :as pi]
            [postmortem.xforms :as pxf]))

(defn update-timer [timer timer-path tick-time]
  (fn [config]
    (let [full-path (conj timer-path timer)
          cur-timer (get-in config full-path)]
      (pm/dump :update-timer)
      (assoc-in config full-path
                (if (not (pos? cur-timer)) 0
                    (- cur-timer tick-time))))))

(defn update-dot [dot tick-time job]
  (fn [config]
    (let [duration   (get-in config [:dots :durations dot])
          ticks      (get-in config [:dots :ticks dot])
          multiplier (get-in config [:dots :multipliers dot])
          potency    (get-in (job :skill-map) [dot :tick-potency])]
      (pm/dump :update-dot)
      (if (and
           (<= (- duration tick-time) (* 3 (dec ticks)))
           (pos? ticks)
           (pos? duration))
        (-> config
            (assoc-in [:dots :ticks dot] (dec ticks))
            (update-in [:total-potency] + (* multiplier potency)))
        config))))

(defn update-buff-ticks [buff tick-time job]
  (fn [config]
    (let [duration        (get-in config [:timers :durations buff])
          ticks           (get-in config [:timers :ticks buff])
          tick-effects    (get-in (job :skill-map) [buff :tick-effects])
          tick-effect-fns (apply comp tick-effects)]
      (pm/dump :update-buff-ticks)
      (if (and
           (<= (- duration tick-time) (* 3 (dec ticks)))
           (pos? ticks)
           (pos? duration))
        (-> config
            tick-effect-fns
            (assoc-in [:timers :ticks buff] (dec ticks)))
        config))))

;; TODO: Auto Attacks -- Maybe not if we just want raw potency
(defn tick-down [config job skill]
  (let [tick-time         ((job :calculate-gcd) config skill)
        update-timers     (fn [timer-path]
                            (map
                             #(update-timer % timer-path tick-time)
                             (keys (get-in config timer-path))))
        update-dots       (fn [dots-path]
                            (map
                             #(update-dot % tick-time job)
                             (keys (get-in config dots-path))))
        update-buff-ticks (fn [buff-ticks-path]
                            (map
                             #(update-buff-ticks % tick-time job)
                             (keys (get-in config buff-ticks-path))))]
    (pm/dump :tick-down)
    ((comp
      (apply comp (update-dots [:dots :durations]))
      (apply comp (update-buff-ticks [:timers :ticks]))
      (apply comp (update-timers [:dots :durations]))
      (apply comp (update-timers [:timers :durations]))
      (apply comp (update-timers [:timers :cooldowns]))) config)))

(defn printutil [returnval & things-to-print]
  (println things-to-print)
  returnval)

;; TODO: Move "NOT APPLIED" into some error area in the response
(defn skill-director [config job skill]
  (let [allowed?          (or (every? true? (map #(% config) (:restrictions skill))) (empty? (:restrictions skill)))
        initial-config    (-> config
                              (update :total-potency + ((job :calculate-potency) config skill))
                              (update :time-elapsed + ((job :calculate-gcd) config skill)))
        all-skill-effects (apply comp (:effects skill))]
    (pm/dump :skill-director)
    (if allowed?
      (all-skill-effects (tick-down initial-config job skill))
      (printutil config "NOT APPLIED" (:name skill) "*********************************"))))

(defn rotation-director [base-config job skills-arr]
  (let [battle-config (merge base-config (job :init))
        mapped-skills (map (job :skill-map) skills-arr)]
    (pm/dump :rotation-director)
    (reduce (fn [config skill] (skill-director config job skill))
            battle-config
            mapped-skills)))
