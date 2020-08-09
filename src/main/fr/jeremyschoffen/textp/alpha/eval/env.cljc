(ns fr.jeremyschoffen.textp.alpha.eval.env
  (:require
    [clojure.spec.alpha :as s]
    [fr.jeremyschoffen.textp.alpha.eval.utils :as u]))


(def ^:dynamic *eval*
  (u/make-error-fn "No *eval* function provided"))

(def ^:dynamic *current-ns*
  (u/make-error-fn "No *current-ns* function provided"))

(def ^:dynamic *in-ns*
  (u/make-error-fn "No *in-ns* function provided"))

(def ^:dynamic *destroy-ns*
  (u/make-error-fn "No *destroy-ns* function provided"))

(def ^:dynamic *read-file*
  (u/make-error-fn "No *slurp* function provided"))

(def ^:dynamic *write-file*
  (u/make-error-fn "No *spit* function provided"))


(s/def ::eval-fn fn?)
(s/def ::current-ns-fn fn?)
(s/def ::in-ns-fn fn?)
(s/def ::remove-ns-fn fn?)
(s/def ::read-file-fn fn?)
(s/def ::write-file-fn fn?)