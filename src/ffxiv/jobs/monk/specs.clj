(ns ffxiv.jobs.monk.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            #_[clojure.spec-alpha2 :as s2]
            #_[clojure.spec-alpha2.gen :as gen2]
            #_[clojure.spec-alpha2.test :as test2]))

(s/def ::total-potency (s/double-in :min 0 :infinity? true))
(s/def ::time-elapsed (s/double-in :min 0 :inifinty? true))

;; JOB-BUFFS
(s/def :job-buffs/form #{:formless :opo :raptor :coeurl :pb})
(s/def :job-buffs/stance #{:stanceless :fire :wind :earth})
(s/def :job-buffs/gl (s/int-in 0 5)) ;; DISCRETE
(s/def :job-buffs/meditation (s/int-in 0 6)) ;; DISCRETE
(s/def :job-buffs/leaden boolean?)
(s/def ::job-buffs (s/keys :req-un [:job-buffs/form
                                    :job-buffs/stance
                                    :job-buffs/gl
                                    :job-buffs/meditation
                                    :job-buffs/leaden]))

;; CHARGES
(s/def :charges/shoulder-tackle (s/int-in 0 3)) ;; DISCRETE
(s/def ::charges (s/keys :req-un [:charges/shoulder-tackle]))

;; TIMERS - COOLDOWNS
(s/def :cd/rof (s/double-in :min 0 :max 90))
(s/def :cd/bh (s/double-in :min 0 :max 90))
(s/def :cd/roe (s/double-in :min 0 :max 80))
(s/def :cd/pb (s/double-in :min 0 :max 90))
(s/def :cd/elixir (s/double-in :min 0 :max 30))
(s/def :cd/shoulder-tackle (s/double-in :min 0 :max 30))
(s/def :cd/tk (s/double-in :min 0 :max 45))
(s/def :cd/anatman (s/double-in :min 0 :max 60))
(s/def :cd/stance (s/double-in :min 0 :max 5))
(s/def :timers/cooldowns (s/keys :req-un [:cd/rof
                                          :cd/bh
                                          :cd/roe
                                          :cd/pb
                                          :cd/elixir
                                          :cd/shoulder-tackle
                                          :cd/tk
                                          :cd/anatman
                                          :cd/stance]))

;; TIMERS - DURATION
(s/def :dur/gl (s/double-in :min 0 :max 16))
(s/def :dur/leaden (s/double-in :min 0 :max 30))
(s/def :dur/twin (s/double-in :min 0 :max 15))
(s/def :dur/rof (s/double-in :min 0 :max 20))
(s/def :dur/bh (s/double-in :min 0 :max 15))
(s/def :dur/roe (s/double-in :min 0 :max 30))
(s/def :dur/pb (s/double-in :min 0 :max 10))
(s/def :timers/durations (s/keys :req-un [:dur/gl
                                          :dur/leaden
                                          :dur/twin
                                          :dur/rof
                                          :dur/bh
                                          :dur/roe
                                          :dur/pb]))

;; TIMERS
(s/def ::timers (s/keys :req-un [:timers/cooldowns
                                 :timers/durations]))

;; DOTS - DURATION
(s/def :durations/demo (s/double-in :min 0 :max 18))
(s/def :dots/durations (s/keys :req-un [:durations/demo]))

;; DOTS - TICKS
(s/def :ticks/demo (s/int-in 0 7)) ;; DISCRETE
(s/def :dots/ticks (s/keys :req-un [:ticks/demo]))

;; DOTS - MULTIPLIER
(s/def :multipliers/demo (s/double-in :min 1 :max 10))
(s/def :dots/multipliers (s/keys :req-un [:multipliers/demo]))

;; DOTS
(s/def ::dots (s/keys :req-un [:dots/durations
                               :dots/ticks
                               :dots/multipliers]))

;; MONK CONFIG
(s/def ::config (s/keys :req-un [::total-potency
                                 ::time-elapsed
                                 ::job-buffs
                                 ::charges
                                 ::timers
                                 ::dots]))

(comment
  (gen/generate (s/gen :dots/durations))

  (gen/generate (s/gen :dots/ticks))

  (gen/generate (s/gen :dots/multipliers))

  (gen/generate (s/gen ::dots))

  (gen/generate (s/gen ::config))

  0)
