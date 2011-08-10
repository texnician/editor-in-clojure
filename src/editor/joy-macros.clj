(require '[clojure.walk :as walk])
(:use '[clojure.data.json :only (json-str write-json read-json)])


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

(xml/emit {:tag :go-component
 :attrs {:name "object-base" :comment "GameObject基本组件"}
 :content [{:tag :go-attribute
            :attrs {:name "id",
                    :type "int",
                    :default 0,
                    :doc "object在游戏运行时唯一的id"
                    :runtime-only "true"}}
           {:tag :go-attribute
            :attrs {:name "asset-type"
                    :type "int"
                    :default 0
                    :doc "object的asset类型"
                    :constraint [:NPC :ITEM :PLANT]}}
           {:tag :go-attribute
            :attrs {:type "int"
                    :default 0
                    :doc "object在所属asset_type集合中的id,在所属asset_type集合中唯一"}}
           {:tag :go-attribute
            :attrs {:name "display-name"
                    :type "string"
                    :default "Base Object"
                    :doc "Oject的名字"}}]}) 
(xml/emit {:tag :go-fruit
 :attrs {:doc "游戏中所有果实的数据"}
 :content
 [{:tag :go
   :attrs {:doc "苹果"}
   :content
   [{:tag :go-component
     :attrs {:id 0 :asset-type :FRUIT :asset-id 9527 :display-name "苹果"}}
    {:tag :go
     :attrs {:doc "鸭梨"}
     :content [{:tag :go-component
                :attrs {:id 0 :asset-type :FRUIT :asset-id 9528 :display-name "鸭梨"}}]}
    {:tag :go
     :attrs {:doc "鸭梨"}
     :content [{:tag :go-component
                :attrs {:id 0 :asset-type :FRUIT :asset-id 9528 :display-name "桃子"}}]}]}]})
(defmacro resolution [] `x)

(def x 9)
(let [x 109] (resolution))

(defmacro awhen [expr & body]
  `(let [~'it ~expr]
     (when ~'it
       (do ~@body))))

(awhen [:a :b :c] (second it))

(import [java.io BufferedReader InputStreamReader]
        [java.net URL])

(defn joc-www []
  (-> "http://www.google.com" URL.
      .openStream InputStreamReader. BufferedReader.))

(let [stream (joc-www)]
  (with-open [page stream]
    (println (.readLine page))
    (print "The stream will now close... "))
  (println "but let's read from it anyway.")
  (.readLine stream))

(partition 3 '(1 2 3 4 5 6 7))


(fn doubler
  ([f x]
     {:post [(= (* 2 x) %)],
      :pre [(pos? x)]}
     (f x)))

(into '[f] '[a b c])

(declare collect-bodies)

(defmacro contract [name & forms]
  (list* `defn name (collect-bodies forms)))

(fn doubler
  ([f x]
     {:post [(= (* 2 x) %)],
      :pre [(pos? x)]}
     (f x)))

(declare build-contract)

(defn collect-bodies [forms]
  (for [form (partition 3 forms)]
    (build-contract form)))

(defn build-contract [c]
  (let [args (first c)]
    (list (into '[f] args)
          (apply merge
                 (for [con (rest c)]
                   (cond (= (first con) :require)
                         (assoc {} :pre (vec (rest con)))
                         (= (first con) :ensure)
                         (assoc {} :post (vec (rest con)))
                         :else (throw (Exception. (str "Unknown tag " (first con)) )))))
          (list* 'f args))))

(first '(:require (pos? x)))
{:pre (vec (rest '(:require (pos? x) (number? x))))}
(macroexpand-1 '(contract doubler
          [x]
          (:require (pos? x))
          (:ensure (= (* 2 x) %))))

(:refer-clojure :exclude [defstruct])
(:use (clojure set xml))
(:use [clojure.test :only (are is)])
(:require (clojure [zip :as z]))
(:import '(java.util Date)
         '(java.io File))


(if-let [[_, v] (find {:a nil :b 2} :a)]
  v)
(find {:a nil :b 2} :a)

(:use (clojure data.json))
(:use [clojure.data.json :only (json-str write-json read-json)])
(clojure.data.json/pprint-json {:tag :go-fruit
 :attrs {:doc "游戏中所有果实的数据"}
 :content
 [{:tag :go
   :attrs {:doc "苹果"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9527 :display-name "苹果"}}]}
  {:tag :go
   :attrs {:doc "鸭梨"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9528 :display-name "鸭梨"}}]}
  {:tag :go
   :attrs {:doc "桃子"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9529 :display-name "桃子"}}]}]} :escape-unicode false)
(xml/emit {:tag :go-fruit
 :attrs {:doc "游戏中所有果实的数据"}
 :content
 [{:tag :go
   :attrs {:doc "苹果"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9527 :display-name "苹果"}}]}
  {:tag :go
   :attrs {:doc "鸭梨"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9528 :display-name "鸭梨"}}]}
  {:tag :go
   :attrs {:doc "桃子"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9529 :display-name "桃子"}}]}]})

(:Card1 + :Card2 = :Card3)

(xml/emit )