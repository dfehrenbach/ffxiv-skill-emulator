(ns ffxiv.jobs.monk.skill-effects
  (:require [ffxiv.jobs.monk.helpers :as helpers]
            [clojure.spec.alpha :as s]
            [ffxiv.jobs.monk.specs :as monk-specs]))

;;; effect
(s/fdef set-stance
  :args (s/cat :stance :job-buffs/stance
               :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn set-stance [new-stance config]
  (if (= (-> config :job-buffs :stance) new-stance)
    (assoc-in config [:job-buffs :stance] :stanceless)
    (assoc-in config [:job-buffs :stance] new-stance)))

;;; effect
(s/fdef reset-stance-duration
  :args (s/cat :stance :job-buffs/stance
               :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-stance-duration [new-stance config]
  (if (= (-> config :job-buffs :stance) new-stance)
    config
    (assoc-in config [:timers :cooldowns :stance] 3.0)))

;;; condition
(s/fdef stance-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn stance-available [config]
  (zero? (-> config :timers :cooldowns :stance)))

;; FORMS
;;; effect
(s/fdef rotate-form
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn rotate-form [config] ;; Error?
  (let [form (-> config :job-buffs :form)]
    (if (pos? (-> config :timers :durations :pb)) (assoc-in config [:job-buffs :form] :pb)
        (cond
          (= form :formless) (assoc-in config [:job-buffs :form] :opo)
          (= form :opo)      (assoc-in config [:job-buffs :form] :raptor)
          (= form :raptor)   (assoc-in config [:job-buffs :form] :coeurl)
          (= form :coeurl)   (assoc-in config [:job-buffs :form] :opo)
          :else              (assoc-in config [:job-buffs :form] :opo)))))

;;; condition
(s/fdef form-restriction
  :args (s/cat :form :job-buffs/form
               :config ::monk-specs/config)
  :ret boolean?)
(defn form-restriction [form config]
  (if (pos? (-> config :timers :durations :pb)) true
      (= form (-> config :job-buffs :form))))

;;; math
(s/fdef formed-multiplier?
  :args (s/cat :skill keyword?
               :config ::monk-specs/config)
  :ret (s/double-in :min 1 :max 3))
(defn formed-multiplier? [skill config]
  (let [crit-damage-mult 1.6]
    (cond
      (and (= skill :boot) (= :opo (-> config :job-buffs :form))) crit-damage-mult
      :else                                                       1.0)))

;;; math
(s/fdef formed-additive?
  :args (s/cat :skill keyword?
               :config ::monk-specs/config)
  :ret (s/int-in 0 31))
(defn formed-additive? [skill config]
  (cond
    (and (= skill :aotd) (= :opo (-> config :job-buffs :form))) 30
    :else                                                       0))

;; GREASED LIGHTNING
;;; effect
(s/fdef drop-gl-stacks
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn drop-gl-stacks [config]
  (assoc-in config [:job-buffs :gl] 0))

;;; effect
(s/fdef add-gl
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn add-gl [config] ;; Error?
  (let [gl (-> config :job-buffs :gl)]
    (if (< gl 4)
      (update-in config [:job-buffs :gl] + 1)
      config)))

;;; effect
(s/fdef reset-gl-timer
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-gl-timer [config]
  (assoc-in config [:timers :durations :gl] 16.0))

;;; effect
(s/fdef form-shift-gl
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn form-shift-gl [config]
  (let [form (-> config :job-buffs :form)]
    (if (or (= form :raptor) (pos? (-> config :timers :durations :pb)))
      (reset-gl-timer config)
      config)))

;;; condition
(s/fdef max-gl
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn max-gl [config]
  (let [gl     (-> config :job-buffs :gl)
        stance (-> config :job-buffs :stance)]
    (cond
      (= stance :stanceless) (= gl 3)
      (= stance :fire)       (= gl 3)
      (= stance :earth)      (= gl 3)
      (= stance :wind)       (= gl 4)
      :else                  (= gl 3))))

;; MEDITATION
;;; effect
(s/fdef use-meditation?
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn use-meditation [config]
  (assoc-in config [:job-buffs :meditation] 0))

;;; effect
(s/fdef add-meditation
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn add-meditation [config]
  (let [meditation (-> config :job-buffs :meditation)]
    (if (< meditation 5)
      (update-in config [:job-buffs :meditation] + 1)
      config)))

;;; condition
(s/fdef max-meditation
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn max-meditation [config]
  (= (-> config :job-buffs :meditation) 5))

;; TWIN SNAKES
;;; effect
(s/fdef reset-twin-snakes
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-twin-snakes [config]
  (assoc-in config [:timers :durations :twin] 15.0))

;; DEMOLISH
;;; effect
(s/fdef reset-demolish
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-demolish [config]
  (-> config
      (assoc-in [:dots :durations :demo] 18.0)
      (assoc-in [:dots :ticks :demo] 6)
      (assoc-in [:dots :multipliers :demo] (helpers/calculate-multiplier config))))

;; LEADEN BOOTSHINE
;;; effect
(s/fdef grant-leaden
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn grant-leaden [config]
  (-> config
      (assoc-in [:job-buffs :leaden] true)
      (assoc-in [:timers :durations :leaden] 30.0)))

;;; effect
(s/fdef use-leaden
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn use-leaden [config]
  (-> config
      (assoc-in [:job-buffs :leaden] false)
      (assoc-in [:timers :durations :leaden] 0.0)))

;;; condition
(s/fdef leaden?
  :args (s/cat :config ::monk-specs/config)
  :ret #{0 150})
(defn leaden? [config]
  (if (-> config :job-buffs :leaden) 150 0))

;; SHOULDER TACKLE
;;; effect
(s/fdef use-st
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn use-st [config]
  (if (pos? (-> config :charges :shoulder-tackle))
    (update-in config [:charges :shoulder-tackle] - 1)
    config))

;;; effect
(s/fdef reset-st-cd
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-st-cd [config]
  (if (= (-> config :charges :shoulder-tackle) 2)
    (assoc-in config [:timers :cooldowns :shoulder-tackle] 30.0)
    config))

;;; condition
(s/fdef st-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn st-available [config]
  (pos? (-> config :charges :shoulder-tackle)))

;; ELIXIR FIELD
;;; effect
(s/fdef reset-elixir-cd
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-elixir-cd [config]
  (assoc-in config [:timers :cooldowns :elixir] 30.0))

;;; condition
(s/fdef elixir-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn elixir-available [config]
  (zero? (-> config :timers :cooldowns :elixir)))

;; TORNADO KICK
;;; condition
(s/fdef tk-avilable
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn tk-available [config]
  (zero? (-> config :timers :cooldowns :tk)))

;; PERFECT BALANCE
;;; effect
(s/fdef reset-pb
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-pb [config]
  (-> config
      (assoc-in [:timers :cooldowns :pb] 120.0)
      (assoc-in [:timers :durations :pb] 10.0)))

;;; condition
(s/fdef pb-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn pb-available [config]
  (zero? (-> config :timers :cooldowns :pb)))

;; RING OF FIRE
;;; effect
(s/fdef reset-rof
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-rof [config]
  (-> config
      (assoc-in [:timers :cooldowns :rof] 90.0)
      (assoc-in [:timers :durations :rof] 20.0)))

;;; condition
(s/fdef rof-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn rof-available [config]
  (zero? (-> config :timers :cooldowns :rof)))

;; BROTHERHOOD
;;; effect
(s/fdef reset-bh
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-bh [config]
  (-> config
      (assoc-in [:timers :cooldowns :bh] 90.0)
      (assoc-in [:timers :durations :bh] 15.0)
      (assoc-in [:timers :ticks :bh] 5)))

;;; condition
(s/fdef bh-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn bh-available [config]
  (zero? (-> config :timers :cooldowns :bh)))

;; ANATMAN
;;; effect
(s/fdef reset-anatman-cd
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-anatman-cd [config]
  (assoc-in config [:timers :cooldowns :anatman] 60.0))

;;; condition
(s/fdef anatman-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn anatman-available [config]
  (zero? (-> config :timers :cooldowns :anatman)))

(comment

  0)
