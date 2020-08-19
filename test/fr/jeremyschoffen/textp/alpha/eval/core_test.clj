(ns fr.jeremyschoffen.textp.alpha.eval.core-test
  (:require
    [clojure.test :refer :all]
    [exoscale.ex :as ex :include-macros true]
    [fr.jeremyschoffen.textp.alpha.eval.core :as eval-core]
    [fr.jeremyschoffen.textp.alpha.eval.env.clojure :as clj-env]))


(deftest evil
  (testing "normal eval"
    (let [program '[(conj [1 2] 3)
                    (+ 1 2 3)]
          res (eval-core/eval-doc-in-temp-ns program clj-env/default)]

      (is (= res
             [(conj [1 2] 3)
              (+ 1 2 3)]))))


  (testing "Exception raised"
    (let [faulty-form '(throw (Exception. "Random exception"))
          faulty-program ['(conj [1 2] 3)
                          faulty-form]
          res (ex/try+
                (eval-core/eval-doc-in-temp-ns faulty-program clj-env/default)
                (catch ::eval-core/eval-error e e))]


      (is (= res
             {:form faulty-form
              :type :fr.jeremyschoffen.textp.alpha.eval.core/eval-error})))))
