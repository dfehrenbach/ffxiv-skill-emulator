(ns ffxiv.jobs.monk.monk
  (:require [ffxiv.jobs.monk.skill-map :as sm]
            [ffxiv.jobs.monk.helpers :as helpers]))

(def init-monk {:total-potency 0
                :time-elapsed  0
                :job-buffs     {:form       :formless
                                :stance     :stanceless
                                :meditation 0
                                :leaden     false}
                :charges       {:shoulder-tackle 2
                                :pb              0}
                :timers        {:cooldowns {:rof             0
                                            :bh              0
                                            :roe             0
                                            :pb              0
                                            :elixir          0
                                            :shoulder-tackle 0
                                            :tk              0
                                            :anatman         0
                                            :stance          0}
                                :durations {:leaden        0
                                            :twin          0
                                            :rof           0
                                            :bh            0
                                            :roe           0
                                            :pb            0
                                            :form          0
                                            :formless-fist 0}
                                :ticks     {:bh 5}}
                :dots          {:durations   {:demo 0}
                                :ticks       {:demo 6}
                                :multipliers {:demo 1}}})

(def monk {:init init-monk
           :skill-map sm/skill-map
           :calculate-potency helpers/calculate-potency
           :calculate-gcd helpers/calculate-gcd})
