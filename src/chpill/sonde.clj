(ns chpill.sonde)


(defmacro spy
  "Emits a `def` for each local binding at the call site, then evaluates the given form."
  [form]
  `(do ~@(map (fn [local-name] `(def ~local-name ~local-name))
              (keys &env))
       ~form))

(comment
  (let [a 1 c 5] (spy (inc a)))
  (let [^:plop a 2 ^:plip c ^{:a :b :c {:d 37}} {}]
    (spy (+ a (inc (count c))))))


(def dummy-nrepl-middleware
  "To be injected alongside the rest of the tooling ahead of the code you'd like
  to probe. This allows the `chpill.sonde/spy` macro to be called from anywhere
  without having to require it first."
  identity)
