(ns fr.jeremyschoffen.textp.alpha.eval.core-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.set :as c-set]
            [fr.jeremyschoffen.textp.alpha.eval.core :as eval-core]
            [fr.jeremyschoffen.textp.alpha.eval.env.clojure :as clj-env]))


(deftest evil
  (let [program '[(conj [1 2] 3)
                  (+ 1 2 3)]
        res (eval-core/eval-doc-in-temp-ns program clj-env/default)]
    (is (= res [(conj [1 2] 3)
                (+ 1 2 3)]))))
