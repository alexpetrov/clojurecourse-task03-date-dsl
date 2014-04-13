(ns halloway-experiments
  "Macros examples form Stuart Halloway Programming Clojure book")

(defmacro bench [expr]
  `(let [start# (System/nanoTime)
         result# ~expr]
     {:result result# :elapsed (- (System/nanoTime) start#)}))

(macroexpand-1 '(bench (str "a" "b")))
(bench (str "a" "b"))

(defmacro unless [cond then else]
  `(if cond
       else
       then))

(macroexpand-1 '(unless (= 1 2)
        (println "Unless")
        (println "Else"))
)

(unless false
        (println "Unless")
        (println "Else"))

(defmacro when-not [expr & body]
  `(if ~expr nil (do ~@body)))

(macroexpand-1 '(when-not (= 1 3) (println "When not") (println "something else")))
(when-not (= 1 3) (println "When not") (println "Something else"))
