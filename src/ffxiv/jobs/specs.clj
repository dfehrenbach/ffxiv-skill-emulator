(ns ffxiv.jobs.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators :as tgen]))

;; effects take a config and return it. Thus Identity
(def effect-fn-generator
  (tgen/return identity))

;; restrictions return either true or false and eventually combine with AND logic
(def restriction-fn-generator
  (tgen/one-of
   [(tgen/return (fn [_config] true))
    (tgen/return (fn [_config] false))]))

;; potency additives eventually are used in calculations but at base return nothing or 0 (identity)
(def potency-additive-fn-generator
  (tgen/return (fn [_config] 0)))

;; potency additives eventually are used in calculations but at base return nothing or 1.0 (identity)
(def potency-multiplier-fn-generator
  (tgen/return (fn [_config] 1.0)))

(s/def ::name (s/and string? #(< (count %) 45)))
(s/def ::time (s/double-in :min 0 :max 10.0))
(s/def ::type #{:gcd :ogcd})
(s/def ::potency (s/int-in 0 5001))
(s/def ::effects (s/with-gen vector? (fn [] (tgen/vector effect-fn-generator 0 5))))
(s/def ::restrictions (s/with-gen vector? (fn [] (tgen/vector restriction-fn-generator 0 5))))
(s/def ::potency-additives (s/with-gen vector? (fn [] (tgen/vector potency-additive-fn-generator 0 5))))
(s/def ::potency-multipliers (s/with-gen vector? (fn [] (tgen/vector potency-multiplier-fn-generator 0 5))))
(s/def ::tick-effects (s/with-gen vector? (fn [] (tgen/vector effect-fn-generator 0 5))))

(s/def ::skill (s/keys :req-un [::name
                                ::time
                                ::type
                                ::potency
                                ::effects
                                ::restrictions]
                       :opt-un [::potency-additives
                                ::potency-multipliers
                                ::tick-effects]))

(comment
  (gen/generate (s/gen ::skill))

  0)
