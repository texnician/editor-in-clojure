(require '[clojure.walk :as walk])

(-> 25 Math/sqrt int list)
(clojure.walk/macroexpand-all '(->> (/ 144 12) (/ ,,, 2 3) str keyword list))
(-> (/ 144 12) (/ 2 3) str)
(mapcat (fn [[k v]] [k `'~v]) '{'a 1, 'b 2})

(defn contextual-eval [ctx expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
      ~expr)))

(contextual-eval {'a 1, 'b 2} '(+ a b))

(contextual-eval {'a 1, 'b 2} '(let [b 1000] (+ a b)))

(defmacro do-until [& clauses]
  (when clauses
    (list `when (first clauses)
          (if (next clauses)
            (second clauses)
            (throw (IllegalArgumentException.
                    "do-until requires an even number of forms")))
          (cons 'do-until (nnext clauses)))))

(macroexpand-1 '
(do-until
 (even? 2) (println "Even")
 (odd? 3) (println "Odd")
 (zero? 1) (println "You never see me")
 :lollipop (println "Truthy thing")))

(defmacro unless [condition & body]
  `(if (not ~condition)
     (do ~@body)))

(unless (= 1 1) (+ 1 1))
(var do-until)

(defmacro domain [name & body]
  `{:tag :domain,
    :attrs {:name (str '~name)},
    :content [~@body]})

(declare handle-things)

(defmacro grouping [name & body]
  `{:tag :grouping,
    :attrs {:name (str '~name)},
    :content [~@(handle-things body)]})

(declare grok-attrs grok-props)

(defn handle-things [things]
  (for [t things]
    {:tag :thing,
     :attrs (grok-attrs (take-while (comp not vector?) t))
     :content (if-let [c (grok-props (drop-while (comp not vector?) t))]
                [c]
                [])}))

(defn grok-attrs [attrs]
  (into {:name (str (first attrs))}
        (for [a (rest attrs)]
          (cond (list? a) [:isa (str (second a))]
                (string? a) [:comment a]))))

(defn grok-props [props]
  (when props
    {:tag :properites, :attrs nil,
     :content (apply vector (for [p props]
                              {:tag :property,
                               :attrs {:name (str (first p))}
                               :content nil}))}))

(def d
  (domain man-vs-monster
          (grouping people
                    (Human "A stock human")
                    (Man (isa Human)
                         "A man, baby"
                         [name]
                         [has-beard?]))
          (grouping monsters
                    (Chupacabra
                     "A fierce, yet elusive creature"
                     [eats-goats?]))))

{:content
 [{:content
   [{:tag :thing,
     :attrs {:name "Human", :comment "A stock human"},
     :content [{:tag :properites, :attrs nil, :content []}]}
    {:tag :thing,
     :attrs {:name "Man", :isa "Human", :comment "A man, baby"},
     :content
     [{:tag :properites,
       :attrs nil,
       :content
       [{:tag :property, :attrs {:name "name"}, :content nil}
        {:tag :property,
         :attrs {:name "has-beard?"},
         :content nil}]}]}],
   :attrs {:name "people"},
   :tag :grouping}
  {:content
   [{:tag :thing,
     :attrs
     {:name "Chupacabra", :comment "A fierce, yet elusive creature"},
     :content
     [{:tag :properites,
       :attrs nil,
       :content
       [{:tag :property,
         :attrs {:name "eats-goats?"},
         :content nil}]}]}],
   :attrs {:name "monsters"},
   :tag :grouping}],
 :attrs {:name "man-vs-monster"},
 :tag :domain}

(use '[clojure.xml :as xml])
(xml/emit d)