(ns macros-experiments
  "Experiments with macros")

(first '(+ 1 2 3))
(eval '(+ 1 2 3))

(list '+ 1 2 3)
(eval (list '+ 1 2 3))

(def data (list '+ 1 2 3))

(eval data)

(def data2 (list 'def 'a 17))

(eval data2)

(defn my-var1 [name value]
  (list 'def name value))

(defn my-var1* [name value]
  `(def ~name ~value))

(defn my-var1* [name value]
  (eval `(def ~name ~value)))

(defmacro my-var2 [name value]
  `(def ~name ~value))

(my-var1* 'd (+ 1 2))
d

(macroexpand-1 '(my-var2 c 20))
(macroexpand-1 '(my-var2 c (+ 10 20)))

(defmacro try-symbols [x y z]
  `(list ~x ~y ~z))

(macroexpand-1 '(try-symbols a b c))
(macroexpand-1 '(try-symbols d e f))

(macroexpand-1 '(try-symbols (+ 1 2) (+ 2 3) (* 4 5)))

(defmacro try-symbols2 [x y z]
  (let [res (+ y z)]
    `(list ~x ~res)))

(macroexpand-1 '(try-symbols2 1 2 3))
#_(macroexpand-1 '(try-symbols2 a b c))


(my-var1* 'c 18)
c

(my-var1 'b 17)
(eval (my-var1 'b 10))
b

'(a b c)
`(a b c)

`(a b)
`(~a ~b)

`(~a 1 2 3 ~b 6 8)

`(let [a# 1
       b# 2]
   (+ a# b#))

(eval `(let [a# 1
       b# 2]
   (+ a# b#))
)

(eval (let [symb-a (gensym "a")
      symb-b (gensym "b")]
 `(let [~symb-a 1
        ~symb-b 2]
    (+ ~symb-a ~symb-b)))
)

(eval (let [symb-a (symbol "a")
            symb-b (symbol "b")]
 `(let [~symb-a 1
        ~symb-b 2]
    (+ ~symb-a ~symb-b)))
)

(def data3 '(1 2 3))

`(+ ~data3)

(eval `(+ ~@data3))
