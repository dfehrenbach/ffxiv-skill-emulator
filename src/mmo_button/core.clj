(ns mmo-button.core
  (:gen-class)
  (:require [clojure.core.async :refer [go-loop chan close!
                                        >! <! put!
                                        buffer sliding-buffer]]
            [mmo-button.calculate :as calc]
            [mmo-button.jobs.monk.monk :refer [monk]]))

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
                :demo :anatman :dk :ts :rof :demo
                :boot :ts :fow :snap
                :dk :pb
                :dk :boot :dk :boot :demo :boot
                :dk :ts :snap
                :dk :true :snap
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

  (let [job (job-switch base-config)
        final (calc/rotation-director base-config job skillset1)]
    {:pot (:total-potency final)
     :time (:time-elapsed final)
     :stance (-> final :job-buffs :stance)
     :pps (/ (:total-potency final) (if (zero? (:time-elapsed final)) 1 (:time-elapsed final)))
     :gl (-> final :job-buffs :gl)
     :pb {:dur (-> final :timers :durations :pb) :cd (-> final :timers :cooldowns :pb)}}))

(-main)
