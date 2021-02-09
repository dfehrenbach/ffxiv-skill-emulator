(ns ffxiv.jobs.monk.skill-map
  (:require [ffxiv.jobs.monk.skill-effects :as effect]))

;; TODO: Build in 5.4 changes
(def skill-map {:boot          {:name                "Bootshine"
                                :time                2.5
                                :type                :gcd
                                :potency             150
                                :potency-additives   [effect/leaden?]
                                :potency-multipliers [(partial effect/formed-multiplier? :boot)]
                                :effects             [effect/rotate-form effect/use-leaden]
                                :restrictions        []}
                :true          {:name         "True Strike"
                                :time         2.5
                                :type         :gcd
                                :potency      240
                                :effects      [effect/rotate-form]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :snap          {:name         "Snap Punch"
                                :time         2.5
                                :type         :gcd
                                :potency      230
                                :effects      [effect/rotate-form effect/add-gl effect/reset-gl-timer]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :dk            {:name         "Dragon Kick"
                                :time         2.5
                                :type         :gcd
                                :potency      200
                                :effects      [effect/rotate-form effect/grant-leaden]
                                :restrictions []}
                :ts            {:name         "Twin Snakes"
                                :time         2.5
                                :type         :gcd
                                :potency      170
                                :effects      [effect/rotate-form effect/reset-twin-snakes]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :demo          {:name         "Demolish"
                                :time         2.5
                                :type         :gcd
                                :potency      90
                                :tick-potency 70
                                :effects      [effect/rotate-form effect/add-gl effect/reset-gl-timer effect/reset-demolish]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :sss           {:name         "Six-Sided Star"
                                :time         5.0
                                :type         :gcd
                                :potency      400
                                :effects      [effect/reset-gl-timer]
                                :restrictions []}
                :tk            {:name         "Tornado Kick"
                                :time         1.0
                                :type         :ogcd
                                :potency      150
                                :effects      [effect/drop-gl-stacks] ; add reset of the TK
                                :restrictions [effect/max-gl effect/tk-available]}
                :st            {:name         "Shoulder Tackle"
                                :time         1.0
                                :type         :ogcd
                                :potency      100
                                :effects      [effect/reset-st-cd effect/use-st]
                                :restrictions [effect/st-available]}
                :elixir        {:name         "Elixir Field"
                                :time         0.5
                                :type         :ogcd
                                :potency      200
                                :effects      [effect/reset-elixir-cd]
                                :restrictions [effect/elixir-available]}
                :tfc           {:name         "The Forbidden Chakra"
                                :time         0.5
                                :type         :ogcd
                                :potency      370
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
                                :potency           80
                                :potency-additives [(partial effect/formed-additive? :aotd)]
                                :effects           [effect/rotate-form]
                                :restrictions      []}
                :fpf           {:name         "Four-point Fury"
                                :time         2.5
                                :type         :gcd
                                :potency      120
                                :effects      [effect/rotate-form effect/reset-twin-snakes]
                                :restrictions [(partial effect/form-restriction :raptor)]}
                :rb            {:name         "Rock Breaker"
                                :time         2.5
                                :type         :gcd
                                :potency      120
                                :effects      [effect/rotate-form effect/add-gl effect/reset-gl-timer]
                                :restrictions [(partial effect/form-restriction :coeurl)]}
                :fow           {:name         "Fist of Wind"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :wind) (partial effect/reset-stance-duration :wind)]
                                :restrictions [effect/stance-available]}
                :foe           {:name         "Fist of Earth"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :earth) (partial effect/reset-stance-duration :earth)]
                                :restrictions [effect/stance-available]}
                :fof           {:name         "Fist of Fire"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [(partial effect/set-stance :fire) (partial effect/reset-stance-duration :fire)]
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
                                :effects      [effect/form-shift-gl effect/rotate-form]
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
                                :effects      [effect/add-gl effect/reset-anatman-cd]
                                :restrictions [effect/anatman-available]}
                :rof           {:name         "Ring of Fire"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :effects      [effect/reset-rof]
                                :restrictions [effect/rof-available]}
                :bh            {:name         "Brotherhood"
                                :time         0.5
                                :type         :ogcd
                                :potency      0
                                :tick-effects [effect/add-meditation]
                                :effects      [effect/reset-bh]
                                :restrictions [effect/bh-available]}})
