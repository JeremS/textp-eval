(ns fr.jeremyschoffen.textp.alpha.eval.core
  (:require
    [net.cgrand.macrovich :as macro :include-macros true]
    [sieppari.core :as sieppari]
    [fr.jeremyschoffen.textp.alpha.eval.env :as env]
    [clojure.tools.namespace.reload :as t-ns]))

(set! *warn-on-reflection* true)


(macro/deftime
  (defmacro binding-env [eval-env & body]
    `(binding [env/*eval*       (::env/eval-fn ~eval-env)
               env/*current-ns* (::env/current-ns-fn ~eval-env)
               env/*in-ns*      (::env/in-ns-fn ~eval-env)
               env/*destroy-ns* (::env/remove-ns-fn ~eval-env)
               env/*read-file*  (::env/read-file-fn ~eval-env)
               env/*write-file* (::env/write-file-fn ~eval-env)]
       ~@body)))


(defn eval-forms
  "Eval several forms in sequence, returns the results of the evaluation in a vector. Needs to be called within an
  evaluation environment.

  See [[textp.eval.alpha.core/binding-env]]"
  [forms]
  (persistent!
    (reduce (fn [acc f] (conj! acc (env/*eval* f)))
            (transient [])
            forms)))


(defn eval-doc
  "Eval a sequence of forms using [[textp.eval.alpha.core]].

  - `forms`: a sequence of clojure forms to evaluate.
  - `env`: an environment map "
  ([forms env]
   (binding-env env
     (eval-forms forms))))


(def snapshot-current-ns-i
  "An interceptor that will add the current ns to the context when entering and put the clojure environment
  to this ns when leaving."
  {:enter (fn [ctxt]
            (assoc ctxt
              ::original-ns (env/*current-ns*)))

   :leave (fn [ctxt]
            (let [{::keys [original-ns]} ctxt]
              (env/*in-ns* original-ns)
              ctxt))})


(defn make-eval-in-ns-i
  "Make an interceptor that will set up a namespace for an evaluation to take place in.
  By default the namespace created for the evaluation is ephemeral.

  - `ns-decl`: A namespace declaration represented by a map.
    For instance in the following:
    ```clojure
    (ns a.ns
      (:require [com.lib.a]
                [com.lib.b]
                :verbose)
      (:import (com.class A B)))
    ```
    the namespace map would be:
    ```clojure
    '{:ns a.ns
      :requirements [(:require [com.lib.a]
                               [com.lib.b]
                               :verbose)
                     (:import (com.class A B))]}```
  - `ephemeral`: set to true by default, this parameter controls if the namespace creted for the evaluation will be
    deleted or not when the evaluation is done, aka when the `:leave` function of the interceptor is called.
  "
  ([ns-decl]
   (make-eval-in-ns-i ns-decl true))
  ([ns-decl ephemeral?]
   {:enter (fn [ctxt]
             (let [{:keys [ns requirements]} ns-decl]
               (env/*eval* (list* 'ns ns requirements))
               (assoc ctxt ::ns-in ns)))

    :leave (fn [ctxt]
             (when ephemeral?
               (let [{::keys [ns-in]} ctxt]
                 (env/*destroy-ns* ns-in)))
             ctxt)

    :error (fn [ctxt]
             (when ephemeral?
               (let [{::keys [ns-in]} ctxt]
                 (env/*destroy-ns* ns-in)))
             ctxt)}))


(defn eval-doc-in-temp-ns
  "Eval a sequence of forms in a temporary namespace. The temporary ns hosting the evaluation is destroyed when the
  evaluation is done, the namespaces created by the evaluated code itself are untouched.

  The evaluation occur at the end of an interceptor chain.
  See:
  - [[textp.eval.alpha.core/snapshot-current-ns-i]]
  - [[textp.eval.alpha.core/make-eval-in-ns-i]]

  Args:
  - `forms`: a sequence of clojure forms to evaluate.
  - `env`: an environment map (see [[textp.eval.alpha.core/binding-env]])
  - `requirements`: a sequence of namespaces requirements. For instance in the following:
    ```clojure
    (ns a.ns
      (:require [com.lib.a]
                [com.lib.b])
      (:import (com.class A B)))
    ```
    the requirements would be:
    ```clojure
    '[(:require [com.lib.a]
                [com.lib.b])
      (:import (com.class A B))]
    ```
  "
  ([forms env]
   (eval-doc-in-temp-ns forms env []))
  ([forms env requirements]
   (let [ns-spec {:ns (gensym "temp-ns_")
                  :requirements requirements}]
     (binding-env env
       (sieppari/execute [snapshot-current-ns-i
                          (make-eval-in-ns-i ns-spec)
                          eval-forms]
                         forms)))))


(comment
  (require '[textp.eval.env.clojure :as cenv])

  (macroexpand-1 '(binding-env cenv/default
                    (env/*read-file* "eval/src/textp/eval/alpha/core.cljc")))

  (binding-env cenv/default
    (env/*read-file* "eval/src/textp/eval/alpha/core.cljc"))

  (slurp "eval/src/textp/eval/core.cljc"))







