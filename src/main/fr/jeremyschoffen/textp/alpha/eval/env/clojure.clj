(ns fr.jeremyschoffen.textp.alpha.eval.env.clojure
  (:require
    [clojure.tools.namespace.reload :as t-ns]
    [fr.jeremyschoffen.textp.alpha.eval.env :as env]))


(def default
  "Default implementation of the function we use to load and eval a document."
  {::env/eval-fn eval
   ::env/current-ns-fn (fn [] (ns-name *ns*))
   ::env/in-ns-fn in-ns
   ::env/remove-ns-fn t-ns/remove-lib
   ::env/read-file-fn slurp
   ::env/write-file-fn spit})