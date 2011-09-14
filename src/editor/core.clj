(ns editor.core
  (:use (clojure pprint))
  (:import (java.util Date))
  (:import (java.text SimpleDateFormat)))

(defn print1 [obj & writer]
  (if-let [w (first writer)]
    (pprint obj w)
    (pprint obj))
  (flush))

(declare collect-bodies)

(defmacro contract [name & forms]
  (list* `fn name (collect-bodies forms)))

(declare build-contract)

(defn collect-bodies [forms]
  (for [form (partition 3 forms)]
    (build-contract form)))

(defn build-contract [c]
  (let [args (first c)]
    (list
     (into '[f] args)
     (apply merge
            (for [con (rest c)]
              (cond (= (first con) :require)
                    (assoc {} :pre (vec (rest con)))
                    (= (first con) :ensure)
                    (assoc {} :post (vec (rest con)))
                    :else (throw (Exception. (str "Unknown tag " (first con)))))))
     (list* 'f args))))

(def doubler-contract
  (contract doubler
            [x y z]
            (:require
             (pos? x))
            (:ensure
             (= (* 2 x) %))))

(defn get-date []
  (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") (Date.)))

(defmacro nif-buggy [expr pos zero neg]
  `(let [~'obscure-name ~expr]
     (cond (pos? ~'obscure-name) ~pos
           (zero? ~'obscure-name) ~zero
           :else ~neg)))

(defmacro nif [expr pos zero neg]
  (let [obscure-name (gensym)]
    `(let [~obscure-name ~expr]
       (cond (pos? ~obscure-name) ~pos
             (zero? ~obscure-name) ~zero
             :else ~neg))))

(nif-buggy 2 (let [obscure-name 'pos]
               obscure-name)
           'zero
           'neg)

(let [obscure-name 'pos]
  (nif-buggy 2 obscure-name
             'zero 'neg))

((fn [obscure-name]
   (nif-buggy 2 obscure-name
              'zero 'neg)) 'pos)

(let [obscure-name 'pos]
  (nif 2 obscure-name
       'zero 'neg))

((fn [obscure-name]
   (nif 2 obscure-name
        'zero 'neg)) 'pos)

(defn recursive-merge-2 [a b]
  (merge-with (fn [x y]
                (if (and (map? x) (map? y))
                  (recursive-merge-2 x y)
                  y)) a b))

(defn recursive-merge [& maps]
  (reduce recursive-merge-2 {} maps))
