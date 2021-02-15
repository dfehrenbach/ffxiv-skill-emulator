(ns ffxiv.core
  (:gen-class)
  (:require [clojure.core.async :refer [go-loop chan close!
                                        >! <! put!
                                        buffer sliding-buffer]]
            [ffxiv.calculate :as calc]
            [ffxiv.jobs.monk.monk :refer [monk]]
            [postmortem.core :as pm]))

(defn default-strategy []
  (let [doing (chan (sliding-buffer 1))
        clean-up (chan (buffer 10000))]
    (go-loop []
      (let [newval (<! doing)]
        (println "operating on" newval)
        (Thread/sleep 2500)
        (>! clean-up newval)
        (println "RESOURCE AVAILABLE")
        (recur)))
    [doing clean-up]))

(def base-config {:job :monk
                  :gcd-strategy 0})

(def skillset1 [:fof :form-shift :form-shift :form-shift
                :meditation :meditation :meditation :meditation :meditation
                :st :demo :anatman :dk :ts :rof :bh :demo
                :boot :tfc :ts :fow :snap :elixir
                :dk :pb
                :dk :st :boot :dk :boot :demo :boot
                :dk :ts :snap
                :dk :true :snap
                :boot :true :demo])

(def skillset2 [:fof :form-shift :form-shift :form-shift
                :meditation :meditation :meditation :meditation :meditation
                :demo :pb :snap :snap
                :ts :rof :fow :demo :bh :tfc :dk :elixir
                :dk :st :true :snap
                :boot :ts :snap
                :dk :true :demo
                :boot :true :snap :st
                :dk :ts :snap
                :boot :true :demo])

(defn job-switch [base-config]
  (case (:job base-config)
    :monk monk
    monk))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (comment (let [[acting-chan clean-up-chan] (:gcd-strategy base-config)]
             (doseq [x (range 30)]
               (println "trying: " x)
               (put! acting-chan x)
               (Thread/sleep 700))
             (close! acting-chan)
             (close! clean-up-chan)))
  (let [job   (job-switch base-config)
        final (calc/rotation-director base-config job [:fof :form-shift :dk])]
    (pm/dump :main)
    (pm/spy>> :results
              {:pot    (:total-potency final)
               :time   (:time-elapsed final)
               :stance (-> final :job-buffs :stance)
               :form   (-> final :job-buffs :form)
               :leaden (-> final :job-buffs :leaden)
               :pps    (/ (:total-potency final) (if (zero? (:time-elapsed final)) 1 (:time-elapsed final)))
               :bh     {:dur    (-> final :timers :durations :bh)
                        :cd     (-> final :timers :cooldowns :bh)
                        :ticks  (-> final :timers :ticks :bh)
                        :stacks (-> final :job-buffs :meditation)}
               :pb     {:dur  (-> final :timers :durations :pb)
                        :cd   (-> final :timers :cooldowns :pb)
                        :uses (-> final :charges :pb)}})))

(comment
  (-main)

  (pm/logs)

  (pm/reset!)

  0)
