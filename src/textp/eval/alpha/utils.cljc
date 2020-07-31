(ns textp.eval.alpha.utils)

(defn make-error-fn
  ([msg]
   (make-error-fn msg {}))
  ([msg data]
   (fn [& _]
     (throw (ex-info msg data)))))