(ns chpill.sonde
  (:require [sc.api]
            [sc.impl :as i]))


;; Heinous ripoff of `sc.api/defsc` that lets you define the var in any namepace
(defmacro internsc
  [ns ep-id]
  (let [cs (i/resolve-code-site ep-id)
        ep-id (i/resolve-ep-id ep-id)]
    (into []
      (map (fn [ln]
             `(intern ~ns (quote ~ln) (i/ep-binding ~ep-id (quote ~ln)))))
      (:sc.cs/local-names cs))))


;; TODO @chpill if we are only interested in the latest value for the context,
;; we could and should get rid of `eval`. To do this, we must understand how
;; scope-capture emits code and reuse/reproduce parts of it.
(defmacro spy
  "VERY experimental macro, use at your own risk.

  Uses the scope-capture library to register context at the call-site, and
  generate bindings in place at runtime. Because of the `eval` inside, we must
  be careful to define the vars in the namespace of the call site. Otherwise,
  the vars are defined in `clojure.core`, which means, in addition to being all
  around nasty, that they won't be properly cleaned up by tools.namespace during
  code reloads."
  [form]
  (let [call-site-ns *ns*]
    `(let [x# (sc.api/spyqt ~form)]
       (eval (list 'chpill.sonde/internsc ~call-site-ns (sc.api/last-ep-id)))
       x#)))

(def nrepl-middleware
  "To be injected with the rest of the tooling ahead of code you'd like to
  probe. This allows the `chpill.sonde/spy` macro to be called from anywhere
  without having to require it inside your code."
  identity)

(comment
  (let [a 4] (spy (inc a)))
  (let [a 4] (macroexpand-1 '(spy (inc a)))))

