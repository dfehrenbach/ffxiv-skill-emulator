(defproject ffxiv "0.1.0-SNAPSHOT"
  :description "playing around with clojure patterns including async. This is a mmo skill sim for ffxiv"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "0.4.500"]
                 [postmortem "0.4.0"]
                 [expound "0.8.7"]
                 [org.clojure/spec.alpha "0.2.194"]
                 [org.clojure/test.check "1.1.0"]]
  :main ^:skip-aot ffxiv.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
