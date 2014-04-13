(ns cemerick-experiments)

(require '(clojure [string :as str]
                   [walk :as walk]))

(defmacro reverse-it [& form]
  (conj (walk/postwalk #(if (symbol? %)
                    (symbol (str/reverse (name %)))
                    %) form) 'do))

(macroexpand-1 '(reverse-it (nltnirp "Hello world") (nltnirp "Hello world")))

(macroexpand-1 '(reverse-it (nltnirp "Hello world")))

(reverse-it (nltnirp "Hello world") (nltnirp "Hello world"))

(do (println "Hello world"))
