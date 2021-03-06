(ns dsl.core
  (:use clojure.walk))

(def cal (java.util.Calendar/getInstance))
(def today (java.util.Date.))
(def yesterday (do (.add cal java.util.Calendar/DATE -1) (.getTime cal)))
(def tomorrow (do (.add cal java.util.Calendar/DATE 2) (.getTime cal)))

(comment
  (defn one [] 1)

  ;; Примеры вызова
  (with-datetime
    (if (> today tomorrow) (println "Time goes wrong"))
    (if (<= yesterday today) (println "Correct"))
    (let [six (+ 1 2 3)
          d1 (today - 2 days)
          d2 (today + 1 week)
          d3 (today + six months)
          d4 (today + (one) year)]
      (if (and (< d1 d2)
               (< d2 d3)
               (< d3 d4))
        (println "DSL works correctly"))))

  (macroexpand-1 '(with-datetime
                    (if (> today tomorrow) (println "Time goes wrong"))
                    (if (<= yesterday today) (println "Correct"))
                    (let [six (+ 1 2 3)
                          d1 (today - 2 days)
                          d2 (today + 1 week)
                          d3 (today + six months)
                          d4 (today + (one) year)]
                      (if (and (< d1 d2)
                               (< d2 d3)
                               (< d3 d4))
                        (println "DSL works correctly"))))
                 )

)


(defn date? [d]
  (instance? java.util.Date d))

;; Поддерживаемые операции:
;; > >= < <=
;; Функция принимает на вход три аргумента. Она должна определить,
;; являются ли второй и третий аргумент датами. Если являются,
;; то из дат необходимо взять date.getTime и сравнить их по этому числу.
;; Если получены не даты, то выполнить операцию op в обычном порядке:
;; (op d1 d2).
(defn d-op [op d1 d2]
  (if (and (date? d1)
           (date? d2))
    (op (.getTime d1) (.getTime d2))
    (op d1 d2)))

(def periods {
                  'day 'java.util.Calendar/DATE
                  'days 'java.util.Calendar/DATE
                  'week 'java.util.Calendar/WEEK_OF_YEAR
                  'weeks 'java.util.Calendar/WEEK_OF_YEAR
                  'month 'java.util.Calendar/MONTH
                  'months 'java.util.Calendar/MONTH
                  'year 'java.util.Calendar/YEAR
                  'years 'java.util.Calendar/YEAR
                  'hour 'java.util.Calendar/HOUR
                  'hours 'java.util.Calendar/HOUR
                  'minute 'java.util.Calendar/MINUTE
                  'minutes 'java.util.Calendar/MINUTE
                  'second 'java.util.Calendar/SECOND
                  'seconds 'java.util.Calendar/SECOND
              })

;; Пример вызова:
;; (d-add today '+ 1 day)
;; Функция должна на основе своих параметров создать новую дату.
;; Дата создается при помощи календаря, например так:
;; (def cal (java.util.Calendar/getInstance))
;; (.setTime cal (java.util.Date))
;; (.add cal java.util.Calendar/DATE 2)
;; (.getTime cal)
;; (d-add cal - 10 year)
;; Во-первых, необходимо на основе 'op' и 'num' определить количество, на
;; которое будем изменять дату. 'Op' может принимать + и -, соответственно
;; нужно будет не изменять либо изменить знак числа 'num'.
;; Во-вторых, необходимо узнать период, на который будем изменять дату.
;; Например, если получили 'day, то в вызове (.add cal ...) будем использовать
;; java.util.Calendar/DATE. Если получили 'months, то java.util.Calendar/MONTH.
;; И так далее.
;; Результат работы функции - новая дата, получаемая из календаря так: (.getTime cal)
(defn cal-from-date [d]
  (let [cal (java.util.GregorianCalendar.)]
    (if (date? d)
      (do (.setTime cal d) cal)
        d)))

(comment
  (cal-from-date (java.util.Date.))
)

(defn d-add [date op num period]
  (let [cal (cal-from-date date)]
    (do
       (.add cal period (op num))
       (.getTime cal)))
  )

;; Можете использовать эту функцию для того, чтобы определить,
;; является ли список из 4-х элементов тем самым списком, который создает новую дату,
;; и который нужно обработать функцией d-add.
(defn is-date-op? [code]
  (let [op (second code)
        period (last code)]
    (and (= (count code) 4)
         (or (= '+ op)
             (= '- op))
         (contains? periods period))))

(defn is-date-cond? [code]
  (let [op (first code)]
    (and (= (count code) 3)
         (contains? #{'> '< '<= '>=} op))))

;; В code содержится код-как-данные. Т.е. сам code -- коллекция, но его содержимое --
;; нормальный код на языке Clojure.
;; Нам необходимо пройтись по каждому элементу этого кода, найти все списки из 3-х элементов,
;; в которых выполняется сравнение, и подставить вместо этого кода вызов d-op;
;; а для списков из четырех элементов, в которых создаются даты, подставить функцию d-add.
(defn replace-period-param [[date op num period]]
  (list date op num (periods period)))

(defn substitute-dsl [form]
  (do #_(println form)
      (cond (not (seq? form)) form
            (is-date-cond? form) (conj form `d-op)
            (is-date-op? form) (conj (replace-period-param form) `d-add)
            :else form)))

(defmacro with-datetime [& code]
  (conj (prewalk substitute-dsl code) `do))
