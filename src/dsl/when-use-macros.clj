(ns when-use-macros
  "Experiments on when use macros lecture"
  (:require [clojure.string :as s]))


;; 1) Преобразование кода
(defn- process-if-seq [data]
  (if (sequential? data) (make-infix data) data))

(defn- make-infix [code]
  (let [fst (first code)
        snd (second code)
        nxt (nnext code)]
    (list snd (process-if-seq fst)
          (if (= (count nxt) 1)
            (process-if-seq (first nxt))
            (make-infix nxt)))))

(eval (make-infix '(1 + (2 * 3) - 4)))
(make-infix '(1 + (2 * 3) - 4))

(defmacro infix [& code]
  (let [prefix (make-infix code)]
    `(~@prefix)))

(infix 1 + (2 * 3) - 4 + 5)

(macroexpand-1 '(infix 1 + (2 * 3) - 4 + 5))
;; 2) bindings
(defmacro with-out-file [& code]
  `(binding [*out* (clojure.java.io/writer "my.txt") ]
     ~@code))

(macroexpand-1 '(with-out-file (println "hello world") (println "1234")))
(with-out-file (println "hello world") (println "1234"))

;; 2) Conditionals
(defmacro if-let* [bindings then else]
  (let [form (bindings 0) tst (bindings 1)]
    `(let [temp# ~tst]
       (if temp#
         (let [~form temp#]
           ~then)
         ~else))))

(macroexpand-1 '(if-let* [x false]
                         (println "then" x)
                         (println "else without x"))
               )

(if-let* [x false]
         (println "then" x)
         (println "else without x"))

;; 5) Использование окружения
(defmacro circle-length [r]
  `(* 2 ~r ~(symbol "pi")))

(def pi 3.14)
(macroexpand-1 '(circle-length 10))
(circle-length 10)

(let [pi 10]
  (circle-length 10))

;; 6) Создание окружения
(def db [{:id 1, :a 2, :b 3} {:id 4, :b 5, :a 6}])

(defn find-by-id [data val]
  (first (filter #(= (get % :id) val) data)))

(find-by-id db 2)

(str 'find-by-id)
(-> (str 'find-by-id)
    (s/split #"-")
    last
    keyword)

(s/split (str 'find-by-id) #"-")

(defmacro make-find-func [name]
  (let [res (-> (str name)
                (s/split #"-")
                last
                keyword)
        ]
    `(defn ~name [data# val#]
       (first (filter #(= (get % ~res) val#) data#)))))

(macroexpand-1 '(make-find-func find-by-a))
(make-find-func find-by-a)
(find-by-a db 6)

;; 7) Сохранение процессорного времени
(defmacro def-sum [name & args]
  (let [res (reduce + args)]
    `(def ~name ~res)))

(macroexpand-1 '(def-sum abc 1 2 3 4 5))
(def-sum abc 1 2 3 4 5)
abc
