(ns ffxiv.jobs.monk.skill-effects
  (:require [ffxiv.jobs.monk.helpers :as helpers]
            [clojure.spec.alpha :as s]
            [ffxiv.jobs.monk.specs :as monk-specs]))

;; TODO: Build in 5.4 changes
;; TODO: Form Shift should not leave you :formless
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
;;; TODO: Send in form and use with partial function to give better workings of :opo
(s/fdef rotate-form
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn rotate-form [config]
  (let [form (-> config :job-buffs :form)]
    (if (and (pos? (-> config :timers :durations :pb)) (pos? (-> config :charges :pb)))
      (-> config
          (assoc-in [:job-buffs :form] :pb)
          (update-in [:charges :pb] dec))
      (cond
        (= form :formless) (assoc-in config [:job-buffs :form] :raptor)
        (= form :pb)       (assoc-in config [:job-buffs :form] :raptor)
        (= form :opo)      (assoc-in config [:job-buffs :form] :raptor)
        (= form :raptor)   (assoc-in config [:job-buffs :form] :coeurl)
        (= form :coeurl)   (assoc-in config [:job-buffs :form] :opo)
        :else              (assoc-in config [:job-buffs :form] :raptor)))))

(s/fdef reset-form-duration
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-form-duration [config]
  (assoc-in config [:timers :durations :form] 15.0))

;;; condition
(s/fdef form-restriction
  :args (s/cat :form :job-buffs/form
               :config ::monk-specs/config)
  :ret boolean?)
(defn form-restriction [form config]
  (if (or (pos? (-> config :timers :durations :pb))
          (pos? (-> config :timers :durations :formless-fist))) true
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
  :ret #{0 30})
(defn formed-additive? [skill config]
  (cond
    (and (= skill :aotd) (= :opo (-> config :job-buffs :form))) 30
    :else                                                       0))

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

;;; effect
(s/fdef refresh-10-twin-snakes
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn refresh-10-twin-snakes [config]
  (let [ts-duration (-> config :timers :durations :twin)
        refreshed-duration (if (<= 15.0 (+ ts-duration 10)) 15.0
                               (+ ts-duration 10))]
    (assoc-in config [:timers :durations :twin] refreshed-duration)))

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
  :ret #{0 170})
(defn leaden? [config]
  (if (-> config :job-buffs :leaden) 170 0))

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
;;; effect
(s/fdef reset-tk-cd
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-tk-cd [config]
  (assoc-in config [:timers :cooldowns :tk] 45.0))

;;; condition
(s/fdef tk-avilable
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn tk-available [config]
  (zero? (-> config :timers :cooldowns :tk)))

;; PERFECT BALANCE
;;; effect
;;; TODO: PB Should set form to :pb
(s/fdef reset-pb
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn reset-pb [config]
  (-> config
      (assoc-in [:timers :cooldowns :pb] 90.0)
      (assoc-in [:timers :durations :pb] 15.0)
      (assoc-in [:charges :pb] 6)))

;;; condition
(s/fdef pb-available
  :args (s/cat :config ::monk-specs/config)
  :ret boolean?)
(defn pb-available [config]
  (zero? (-> config :timers :cooldowns :pb)))

;; RIDDLE OF FIRE
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

;; FORM SHIFT
;;; effect
(s/fdef set-formless-fist
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn set-formless-fist [config]
  (-> config
      (assoc-in [:timers :durations :form] 15.0)
      (assoc-in [:timers :durations :formless-fist] 15.0)))

;;; effect
(s/fdef unset-formless-fist
  :args (s/cat :config ::monk-specs/config)
  :ret ::monk-specs/config)
(defn unset-formless-fist [config]
  (assoc-in config [:timers :durations :formless-fist] 0))

(comment

  0)
