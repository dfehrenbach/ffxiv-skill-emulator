(ns ffxiv.jobs.monk.skill-map
  (:require [ffxiv.jobs.monk.skill-effects :as effect]))

;; TODO: Build in 5.4 changes
;; TODO: Add in Mantra
;; TODO: Add in Riddle of Earth
;; TODO: Refactor to include CD time HERE instead of in the config. Make healpert to construct config with values
;; TODO: Add in level requirements
;; TODO: Add in a "wait"
;; TODO: Allow the ability to "Hold" a skill (Anatman)
(def skill-map {:boot          {:name                "Bootshine"
                                :time                2.5
                                :type                :gcd
                                :potency             200
                                :potency-additives   [effect/leaden?]
                                :potency-multipliers [(partial effect/formed-multiplier? :boot)]
                                :effects             [effect/rotate-form
                                                      effect/reset-form-duration
                                                      effect/unset-formless-fist
                                                      effect/use-leaden]
                                :restrictions        []}
                :true          {:name         "True Strike"
                                :time         2.5
                                :type         :gcd
                                :potency      300
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :snap          {:name         "Snap Punch"
                                :time         2.5
                                :type         :gcd
                                :potency      300
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :dk            {:name         "Dragon Kick"
                                :time         2.5
                                :type         :gcd
                                :potency      200
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist
                                               effect/grant-leaden]
                                :restrictions []}
                :ts            {:name         "Twin Snakes"
                                :time         2.5
                                :type         :gcd
                                :potency      260
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist
                                               effect/reset-twin-snakes]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :demo          {:name         "Demolish"
                                :time         2.5
                                :type         :gcd
                                :potency      110
                                :tick-potency 80
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist
                                               effect/reset-demolish]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :sss           {:name         "Six-Sided Star"
                                :time         5.0
                                :type         :gcd
                                :potency      540
                                :effects      []
                                :restrictions []}
                :tk            {:name         "Tornado Kick"
                                :time         0.5
                                :type         :ogcd
                                :potency      400
                                :effects      [effect/reset-tk-cd]
                                :restrictions [effect/tk-available]}
                :st            {:name         "Shoulder Tackle"
                                :time         1.0
                                :type         :ogcd
                                :potency      100
                                :effects      [effect/reset-st-cd
                                               effect/use-st]
                                :restrictions [effect/st-available]}
                :elixir        {:name         "Elixir Field"
                                :time         0.5
                                :type         :ogcd
                                :potency      250
                                :effects      [effect/reset-elixir-cd]
                                :restrictions [effect/elixir-available]}
                :tfc           {:name         "The Forbidden Chakra"
                                :time         0.5
                                :type         :ogcd
                                :potency      340
                                :effects      [effect/use-meditation]
                                :restrictions [effect/max-meditation]}
                :enlightenment {:name         "Enlightenment"
                                :time         0.5
                                :type         :ogcd
                                :potency      220
                                :effects      [effect/use-meditation]
                                :restrictions [effect/max-meditation]}
                :aotd          {:name              "Arm of the Destroyer"
                                :time              2.5
                                :type              :gcd
                                :potency           110
                                :potency-additives [(partial effect/formed-additive? :aotd)]
                                :effects           [effect/rotate-form
                                                    effect/reset-form-duration
                                                    effect/unset-formless-fist]
                                :restrictions      []}
                :fpf           {:name         "Four-point Fury"
                                :time         2.5
                                :type         :gcd
                                :potency      140
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist
                                               effect/refresh-10-twin-snakes]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :rb            {:name         "Rock Breaker"
                                :time         2.5
                                :type         :gcd
                                :potency      150
                                :effects      [effect/rotate-form
                                               effect/reset-form-duration
                                               effect/unset-formless-fist]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :fow           {:name         "Fist of Wind"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :wind)
                                               (partial effect/reset-stance-duration :wind)]
                                :restrictions [effect/stance-available]}
                :foe           {:name         "Fist of Earth"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :earth)
                                               (partial effect/reset-stance-duration :earth)]
                                :restrictions [effect/stance-available]}
                :fof           {:name         "Fist of Fire"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :fire)
                                               (partial effect/reset-stance-duration :fire)]
                                :restrictions [effect/stance-available]}
                :pb            {:name         "Perfect Balance"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [effect/reset-pb]
                                :restrictions [effect/pb-available]}
                :form-shift    {:name         "Form Shift"
                                :time         2.5
                                :type         :gcd
                                :potency      0
                                :effects      [effect/set-formless-fist]
                                :restrictions []}
                :meditation    {:name         "Meditation"
                                :time         1.2
                                :type         :gcd
                                :potency      0
                                :effects      [effect/add-meditation]
                                :restrictions []}
                :anatman       {:name         "Anatman"
                                :time         1.2
                                :type         :ogcd
                                :potency      0
                                :effects      [effect/reset-anatman-cd
                                               effect/reset-twin-snakes
                                               effect/reset-form-duration]
                                :restrictions [effect/anatman-available]}
                :rof           {:name         "Riddle of Fire"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [effect/reset-rof]
                                :restrictions [effect/rof-available]}
                :roe           {:name         "Riddle of Earth"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [] ;; TODO: Riddle of Earth effects "use-roe" look at shoulder tackle
                                :restrictions []} ;; TODO: Riddle of Earth restrictions "has charge to use" look at shoulder tackle
                :bh            {:name         "Brotherhood"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :tick-effects [effect/add-meditation]
                                :effects      [effect/reset-bh]
                                :restrictions [effect/bh-available]}})
