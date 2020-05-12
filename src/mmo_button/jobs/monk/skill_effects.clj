(ns mmo-button.jobs.monk.skill-effects
  (:require [mmo-button.jobs.monk.helpers :as helpers]
            [clojure.spec.alpha :as s]
            [mmo-button.jobs.monk.specs :as monk-specs]
            [ghostwheel.core :as g
             :refer [>defn =>]]))

(>defn set-stance [new-stance config]
       [:job-buffs/stance ::monk-specs/config => ::monk-specs/config]
       (if (= (-> config :job-buffs :stance) new-stance)
         (assoc-in config [:job-buffs :stance] :stanceless)
         (assoc-in config [:job-buffs :stance] new-stance)))

(>defn reset-stance-duration [new-stance config]
       [:job-buffs/stance ::monk-specs/config => ::monk-specs/config]
       (if (= (-> config :job-buffs :stance) new-stance)
         config
         (assoc-in config [:timers :cooldowns :stance] 3.0)))

(>defn stance-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :stance)))

;; FORMS
(>defn rotate-form [config] ;; Error?
       [::monk-specs/config => ::monk-specs/config]
       (let [form (-> config :job-buffs :form)]
         (if (pos? (-> config :timers :durations :pb)) (assoc-in config [:job-buffs :form] :pb)
             (cond
               (= form :formless) (assoc-in config [:job-buffs :form] :opo)
               (= form :opo) (assoc-in config [:job-buffs :form] :raptor)
               (= form :raptor) (assoc-in config [:job-buffs :form] :coeurl)
               (= form :coeurl) (assoc-in config [:job-buffs :form] :opo)
               :else (assoc-in config [:job-buffs :form] :opo)))))

(>defn form-restriction [form config]
       [:job-buffs/form ::monk-specs/config => boolean?]
       (if (pos? (-> config :timers :durations :pb)) true
           (= form (-> config :job-buffs :form))))

(>defn formed-multiplier? [skill config]
       [keyword? ::monk-specs/config => (s/double-in :min 1 :max 3)]
       (let [crit-damage-mult 1.6]
         (cond
           (and (= skill :boot) (= :opo (-> config :job-buffs :form))) crit-damage-mult
           :else 1.0)))

(>defn formed-additive? [skill config]
       [keyword? ::monk-specs/config => (s/int-in 0 31)]
       (cond
         (and (= skill :aotd) (= :opo (-> config :job-buffs :form))) 30
         :else 0))

;; GREASED LIGHTNING
(>defn drop-gl-stacks [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:job-buffs :gl] 0))

(>defn add-gl [config] ;; Error?
       [::monk-specs/config => ::monk-specs/config]
       (let [gl (-> config :job-buffs :gl)]
         (if (< gl 4)
           (update-in config [:job-buffs :gl] + 1)
           config)))

(>defn reset-gl-timer [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:timers :durations :gl] 16.0))

(>defn form-shift-gl [config] ;; Error?
       [::monk-specs/config => ::monk-specs/config]
       (let [form (-> config :job-buffs :form)]
         (if (or (= form :raptor) (pos? (-> config :timers :durations :pb)))
           (reset-gl-timer config)
           config)))

(>defn max-gl [config]
       [::monk-specs/config => boolean?]
       (let [gl (-> config :job-buffs :gl)
             stance (-> config :job-buffs :stance)]
         (cond
           (= stance :stanceless) (= gl 3)
           (= stance :fire) (= gl 3)
           (= stance :earth) (= gl 3)
           (= stance :wind) (= gl 4)
           :else (= gl 3))))

;; MEDITATION
(>defn use-meditation [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:job-buffs :meditation] 0))

(>defn add-meditation [config] ;; Error???
       [::monk-specs/config => ::monk-specs/config]
       (let [meditation (-> config :job-buffs :meditation)]
         (if (< meditation 5)
           (update-in config [:job-buffs :meditation] + 1)
           config)))

(>defn max-meditation [config]
       [::monk-specs/config => boolean?]
       (= (-> config :job-buffs :meditation) 5))

;; TWIN SNAKES
(>defn reset-twin-snakes [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:timers :durations :twin] 15.0))

;; DEMOLISH
(>defn reset-demolish [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:dots :durations :demo] 18.0)
           (assoc-in [:dots :ticks :demo] 6)
           (assoc-in [:dots :multipliers :demo] (helpers/calculate-multiplier config))))

;; LEADEN BOOTSHINE
(>defn grant-leaden [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:job-buffs :leaden] true)
           (assoc-in [:timers :durations :leaden] 30.0)))

(>defn use-leaden [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:job-buffs :leaden] false)
           (assoc-in [:timers :durations :leaden] 0.0)))

(>defn leaden? [config]
       [::monk-specs/config => (s/int-in 0 151)]
       (if (-> config :job-buffs :leaden) 150 0))

;; SHOULDER TACKLE
(>defn use-st [config]
       [::monk-specs/config => ::monk-specs/config]
       (if (pos? (-> config :charges :shoulder-tackle))
         (update-in config [:charges :shoulder-tackle] - 1)
         config))

(>defn reset-st-cd [config]
       [::monk-specs/config => ::monk-specs/config]
       (if (= (-> config :charges :shoulder-tackle) 2)
         (assoc-in config [:timers :cooldowns :shoulder-tackle] 30.0)
         config))

(>defn st-available [config]
       [::monk-specs/config => boolean?]
       (pos? (-> config :charges :shoulder-tackle)))

;; ELIXIR FIELD
(>defn reset-elixir-cd [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:timers :cooldowns :elixir] 30.0))

(>defn elixir-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :elixir)))

;; TORNADO KICK
(>defn tk-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :tk)))

;; PERFECT BALANCE
(>defn reset-pb [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:timers :cooldowns :pb] 120.0)
           (assoc-in [:timers :durations :pb] 10.0)))

(>defn pb-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :pb)))

;; RING OF FIRE
(>defn reset-rof [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:timers :cooldowns :rof] 90.0)
           (assoc-in [:timers :durations :rof] 20.0)))

(>defn rof-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :rof)))

;; BROTHERHOOD
(>defn reset-bh [config]
       [::monk-specs/config => ::monk-specs/config]
       (-> config
           (assoc-in [:timers :cooldowns :bh] 90.0)
           (assoc-in [:timers :durations :bh] 15.0)))

(>defn bh-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :bh)))

;; ANATMAN
(>defn reset-anatman-cd [config]
       [::monk-specs/config => ::monk-specs/config]
       (assoc-in config [:timers :cooldowns :anatman] 60.0))

(>defn anatman-available [config]
       [::monk-specs/config => boolean?]
       (zero? (-> config :timers :cooldowns :anatman)))

(g/check)
