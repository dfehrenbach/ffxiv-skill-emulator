(ns ffxiv.jobs.red-mage.skill-map
  (:require [ffxiv.jobs.redmage.skill-effects :as effect]))

(def skill-map
  ;; Single Casts
  {:jolt2          {:name      "Jolt II"
                    :time      2.5
                    :cast-time 2.0
                    :type      :gcd
                    :potency   290
                    :effects   [(partial effect/add-black-mana 3)
                                (partial effect/add-white-mana 3)
                                effect/add-doublecast
                                effect/use-doublecast]}
   :verfire        {:name         "Verfire"
                    :time         2.5
                    :cast-time    2.0
                    :type         :gcd
                    :potency      310
                    :effects      [(partial effect/add-black-mana 9)
                                   effect/use-verfire-ready
                                   effect/add-doublecast
                                   effect/use-doublecast]
                    :restrictions [effect/verfire-ready?]}
   :verstone       {:name         "Verstone"
                    :time         2.5
                    :cast-time    2.0
                    :type         :gcd
                    :potency      310
                    :effects      [(partial effect/add-white-mana 9)
                                   effect/use-verstone-ready
                                   effect/add-doublecast
                                   effect/use-doublecast]
                    :restrictions [effect/verstone-ready?]}
   :verthunder     {:name      "Verthunder"
                    :time      2.5
                    :cast-time 5.0
                    :type      :gcd
                    :potency   370
                    :effects   [(partial effect/add-black-mana 11)
                                (partial effect/random-add-verfire-ready {:chance 50})
                                effect/add-doublecast
                                effect/use-doublecast]}
   :veraero        {:name      "Veraero"
                    :time      2.5
                    :cast-time 5.0
                    :type      :gcd
                    :potency   370
                    :effects   [(partial effect/add-white-mana 11)
                                (partial effect/random-add-verstone-ready {:chance 50})
                                effect/add-doublecast
                                effect/use-doublecast]}
   :verflare       {:name         "Verflare"
                    :time         2.5
                    :cast-time    0
                    :type         :gcd
                    :potency      600
                    :effects      [(partial effect/add-black-mana 21)
                                   (partial effect/random-add-verfire-ready {:chance   20
                                                                             :verflare true})
                                   effect/use-verflare-combo
                                   effect/use-verholy-combo
                                   effect/add-scorch-combo]
                    :restrictions [effect/verflare-combo?]}
   :verholy        {:name         "Verholy"
                    :time         2.5
                    :cast-time    0
                    :type         :gcd
                    :potency      600
                    :effects      [(partial effect/add-white-mana 21)
                                   (partial effect/random-add-verfire-ready {:chance  20
                                                                             :verholy true})
                                   effect/use-verholy-combo
                                   effect/use-verflare-combo
                                   effect/add-scorch-combo]
                    :restrictions [effect/verholy-combo?]}
   :scorch         {:name         "Scorch"
                    :time         2.5
                    :cast-time    0
                    :type         :gcd
                    :potency      700
                    :effects      [(partial effect/add-black-mana 7)
                                   (partial effect/add-white-mana 7)
                                   effect/use-scorch-combo]
                    :restrictions [effect/scorch-combo?]}

   ;; AOE Casts
   :impact         {:name      "Verthunder II"
                    :time      2.5
                    :cast-time 5.0
                    :type      :gcd
                    :potency   220
                    :effects   [(partial effect/add-white-mana 3)
                                (partial effect/add-black-mana 3)
                                effect/add-doublecast
                                effect/use-doublecast]}
   :verthunder2    {:name      "Verthunder II"
                    :time      2.5
                    :cast-time 2.0
                    :type      :gcd
                    :potency   120
                    :effects   [(partial effect/add-black-mana 7)
                                effect/add-doublecast
                                effect/use-doublecast]}
   :veraero2       {:name      "Veraero II"
                    :time      2.5
                    :cast-time 2.0
                    :type      :gcd
                    :potency   120
                    :effects   [(partial effect/add-white-mana 7)
                                effect/add-doublecast
                                effect/use-doublecast]}

   ;; Sword Skills
   :riposte        {:name              "Riposte"
                    :time              2.5
                    :time-reductions   [(partial effect/enchanted? {:cost      30
                                                                    :reduction 1.0})]
                    :cast-time         0
                    :type              :gcd
                    :potency           130
                    :potency-additives [(partial effect/enchanted-bonus? {:cost  30
                                                                          :bonus 90})]
                    :effects           [(partial effect/use-both-mana 30)
                                        effect/add-zwerchhau-combo]}
   :zwerchhau      {:name              "Zwerchhau"
                    :time              2.5
                    :time-reductions   [(partial effect/enchanted? {:cost      25
                                                                    :reduction 1.0})]
                    :cast-time         0
                    :type              :gcd
                    :potency           100
                    :potency-additives [effect/zwerchhau-combo? ;; 50
                                        (partial effect/enchanted-bonus? {:cost  25
                                                                          :bonus 140
                                                                          :combo :zwerchhau})] ;; enchanted combo 290 
                    :effects           [(partial effect/use-both-mana 25)
                                        effect/use-zwerchhau-combo
                                        effect/add-roudblement-combo]}
   :redoublement   {:name              "Redoublement"
                    :time              2.5
                    :time-reductions   [(partial effect/enchanted? {:cost      25
                                                                    :reduction 0.3})]
                    :cast-time         0
                    :type              :gcd
                    :potency           100
                    :potency-additives [effect/redoublement-combo? ;; 130
                                        (partial effect/enchanted-bonus? {:cost  25
                                                                          :bonus 240
                                                                          :combo :redoublement})] ;; enchanted combo 470 
                    :effects           [(partial effect/use-both-mana 25)
                                        effect/use-redoublement-combo
                                        effect/add-verholy-combo
                                        effect/add-verflare-combo]}
   :moulinet       {:name              "Moulinet"
                    :time              2.5
                    :time-reductions   [(partial effect/enchanted? {:cost      20
                                                                    :reduction 1.0})]
                    :cast-time         0
                    :type              :gcd
                    :potency           60
                    :potency-additives [(partial effect/enchanted-bonus? {:cost  20
                                                                          :bonus 140})]
                    :effects           [(partial effect/use-both-mana 20)]}
   :reprise        {:name              "Reprise"
                    :time              2.5
                    :time-reductions   [(partial effect/enchanted? {:cost      5
                                                                    :reduction 0.3})]
                    :cast-time         0
                    :type              :gcd
                    :potency           100
                    :potency-additives [(partial effect/enchanted-bonus? {:cost  5
                                                                          :bonus 200})]
                    :effects           [(partial effect/use-both-mana 5)]}

   ;; Movement skills OGCDS
   :corps-a-corps  {}
   :displacement   {}
   :engagement     {}

   ;; OGCDS
   :fleche         {}
   :contre-sixte   {}

   ;; Buffs
   :acceleration   {}
   :embolden       {}
   :manafication   {}

   ;; Heals/Raise
   :vercure        {}
   :verraise       {}

   ;; Utility
   :swiftcast      {}
   :addle          {}
   :lucid-dreaming {}
   :surecast       {}})
