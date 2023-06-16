# Sonde

### Rationale

To debug something at the Clojure REPL, have you ever manually instrumented your code in this way?

```
(defn my-func [a b]
  (let [c (internal-func a b)]
    (def a a)  ;;
    (def b b)  ;; <= manual and tedious instrumentation
    (def c c)  ;;
    (compute-result a b c)))
```

This library provides a [dumb](src/chpill/sonde.clj#L4-L9) macro that does it for you:

```
(defn my-func [a b]
  (let [c (internal-func a b)]
    (chpill.sonde/spy
     (compute-result a b c))))
```

### How to use it

Clone this repo, and add this to your dev dependencies `{chpill/sonde {:local/root "../path/to/clone"}}`.

In order to be able to call this macro from anywhere in your code without having
to `require` it first, and are [nREPL](https://github.com/nrepl/nrepl) using you
may be interested in the `chpill.sonde/dummy-nrepl-middleware`. For example,
with Emacs and Cider, you can put this in your `.dir-locals.el` or
`.dir-locals-2.el`:

```
((nil . ((eval . (progn
                   (make-variable-buffer-local 'cider-jack-in-nrepl-middlewares)
                   (add-to-list 'cider-jack-in-nrepl-middlewares "chpill.sonde/dummy-nrepl-middleware"))))))
```

 Another way to gain this power is to use a custom data reader as described [here](https://github.com/vvvvalvalval/scope-capture/blob/59b2261bd90b6bbd81faed8a5c0149864bd92b8c/doc/Tips-and-Tricks.md#adding-a-reader-macro-shorthand).

*NB* Using this macro will polute your namespaces, so it is best used alongside
other tooling to reload code, such as [tools.namespace](https://github.com/clojure/tools.namespace).

### Clojurescript?

No.

### Credits

The idea for this macro was inspired by the use of the [Scope
Capture](https://github.com/vvvvalvalval/scope-capture) library. Many thanks to
its author, you may check the initial commit of this repo to see how it can be
used to produce the same effect. As I commonly used only a tiny subset of the
features of the library, I decided to write this standalone macro so that my
dumb common use case could stay dumb.

TODO write about how to install/inject it in a REPL, and about basic usage
