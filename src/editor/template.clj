(ns editor.template
  (:use (editor domain component core error))
  (:import (java.lang Exception
                      IllegalArgumentException)))

;; {:tag :go-template
;;  :attrs {:name "fruit" :doc "果实的模板"}
;;  :content [{:tag :go-component
;;             :attrs {:name "base" :doc "基本组件"}
;;             :content [{:tag :id
;;                        :attrs {:type "int" :doc "Domain内的唯一id"}
;;                        :content ["0"]}
;;                       {:tag :name
;;                        :attrs {:type "string" :doc "Object的名字"}
;;                        :content ["(Unamed)"]}]}
;;            {:tag :go-component
;;             :attrs {:name "item-base" :doc "物品的基本属性"}
;;             :content {{:tag :max-own-num
;;                        :attrs {:type "int" :doc "物品的最大持有数量"}
;;                        :content ["99999"]}
;;                       {:tag :item-lifetime
;;                        :attrs {:type "int" :doc "物品存在时间"}
;;                        :content ["0"]}}}]}

(defn make-template-node [temp-name doc & components]
  "生成一个模板节点， components为组件列表:((:组件名 <{:默认参数 默认值>}>) <(:组件名 <{:默认参数 默认值>}>)*)"
  {:tag :go-template
   :attrs {:name (name temp-name)
           :doc doc}
   :content (apply vector (map (fn [x]
                                (apply make-component-node x)) components))})


;; (register-go-template :fruit (make-template-node :fruit "果实的模板"
;;                                                  '(:base ) '(:item-base {:item-lifetime 10}) '(:trade)))

(declare register-go)

(defmacro deftemplate [temp-name & body]
  (let [key-name (keyword temp-name)
        [doc & comp-list] (if (string? (first body))
                            body
                            (conj body ""))
        vals (gensym)
        vals-2 (gensym)
        fvals (gensym)
        x (gensym)]
    `(do (register-in-domain :go-template ~key-name
                             (make-template-node ~key-name ~doc
                                                 ~@(map (fn [y]
                                                          `'~(conj (if-let [attr-vals (next y)]
                                                                     (list (apply array-map attr-vals))
                                                                     nil) (keyword (first y)))) comp-list)))
         (defn ~(symbol (str "def" (str temp-name) "-fn")) [& ~vals]
           {:pre [(-> ~vals count even?)
                  (some #{:id} ~vals)]}
           (register-go ~key-name (apply array-map ~vals)))
         (defmacro ~(symbol (str "def" (str temp-name))) [& ~vals-2]
           (let [~fvals (map (fn [~x]
                               (if-not (or (keyword? ~x) (integer? ~x) (string? ~x)) (str ~x)
                                       ~x)) ~vals-2)]
             `(~'~(symbol (str "def" (str temp-name) "-fn")) ~@~fvals))))))

(defn- extract-attribute-key [node]
  (:tag node))

(defn- extract-attribute-value [comp-key attr-key node node-type]
  (if (= node-type :template-node)
    (-> node :content first)
    (if (atom-attribute? comp-key attr-key)
      (-> node :content first)
      (str (vec (map (fn [x]
                       (-> x :content first read-string)) (-> node :content)))))))

(defn- extract-attribute-list [comp-key node-type attr-nodes]
  (into {} (map (fn [c]
                  (let [attr-key (extract-attribute-key c)]
                    [attr-key (extract-attribute-value comp-key attr-key c node-type)])) attr-nodes)))

(defn node->concrete-object [node node-type]
  "Convert xml node to inernal concrete object"
  (into {} (map (fn [x]
                  (let [comp-key (-> x :attrs :name keyword)]
                    [comp-key
                     (if-let [content (:content x)]
                       (extract-attribute-list comp-key node-type content))])) (:content node))))

(defn make-concrete-template [template-key]
  (if-let [template-node ((get-domain :go-template) template-key)]
    (node->concrete-object template-node :template-node)))

(def input-attribute-contract
  (contract input-attribute [attr-key comps]
            (:require (keyword? attr-key)
                      (not-empty comps)
                      (every? keyword comps))
            (:ensure (and "属性不存在" (keyword? %)))))

(defn find-attribute-component [attr-key comps]
  (some (fn [x]
          (if (contains? (set (component-attribute-keys x)) attr-key)
            x)) comps))

;; (defn assoc-concrete-attribute-value [concrete-template attr-key value]
;;   (let [comp-key ((partial input-attribute-contract find-attribute-component) attr-key (keys concrete-template))
;;         comp-attrs (concrete-template comp-key)]
;;     (assoc concrete-template comp-key (assoc comp-attrs attr-key value))))

(defn assoc-concrete-attribute-value [concrete-template attr-key value]
  (let [comp-key (find-attribute-component attr-key (keys concrete-template))
        comp-attrs (concrete-template comp-key)]
    (if (keyword? comp-key)
      (assoc concrete-template comp-key (assoc comp-attrs attr-key value))
      (emit-error :attribute-not-exist {:attr (name attr-key)}))))

(defn get-attribute-value [obj attr-key]
  (let [comp-key ((partial input-attribute-contract find-attribute-component) attr-key (keys obj)) 
        comp-attrs (obj comp-key)]
    (attr-key comp-attrs)))

(defn make-concrete-game-object [template-key attr-map]
  "返回一个game object, 并设置好attr-map中的属性值"
  (let [temp-obj (make-concrete-template template-key)]
    (reduce (fn [obj attr-pair]
              (assoc-concrete-attribute-value obj (first attr-pair) (second attr-pair)))
            temp-obj attr-map)))


;; {:id 2, :name "鸭梨"}

;; [{:tag :id,
;;   :content ["2"]}
;;  {:tag :name,
;;   :content ["鸭梨"]}]
;;; or
;;; :content [{:tag :item
;;;            :content ["1"]}
;;;           {:tag :item
;;;            :content ["2"]}]
(defn make-array-attribute-value [comp-key attr-key str-value]
  (let [value (read-string str-value)]
    (if (vector? value)
      (let [meta-info (get-attribute-meta-info comp-key attr-key #{:type :in-domain})]
        (apply vector (map (fn [x]
                             {:tag :item
                              :attrs (merge {:type (:type meta-info)}
                                            (attribute-extend-attrs attr-key meta-info x)) 
                              :content [(str x)]}) value)))
      (emit-error :array-attribute-value-not-vector {:attr (name attr-key) :value value}))))

(defn make-game-object-attributes [comp-key, attrs]
  "返回game object attributes"
  (into [] (map (fn [x]
                  (let [[attr-key value] x]
                    (merge {:tag attr-key}
                           (if (atom-attribute? comp-key attr-key)
                             {:attrs (attribute-extend-attrs attr-key
                                                             (get-attribute-meta-info comp-key attr-key #{:type :in-domain})
                                                             value)
                              :content [(str value)]}
                             {:attrs (get-attribute-meta-info comp-key attr-key #{:size})
                              :content (make-array-attribute-value comp-key attr-key value)}))))
                attrs)))

;; {:base {:id 2, :name "鸭梨"},
;;  :item-base {:max-own-num 19, :item-lifetime "10"},
;;  :trade
;;  {:is-tradable false,
;;   :buy-price "0",
;;   :sell-price "0",
;;   :repair-price "0",
;;   :is-gift "true"}}

;; [{:tag :go-component,
;;   :attrs {:name "base"},
;;   :content [{:tag :id,
;;              :content ["2"]}
;;             {:tag :name,
;;              :content ["鸭梨"]}]}
;;  {:tag :go-component,
;;   :attrs {:name "item-base"},
;;   :content [{:tag :max-own-num,
;;              :content ["99"]}
;;             {:tag :item-lifetime,
;;              :content ["0"]}]}]

(defn make-game-object-components [obj]
  "返回最终的game object components"
  (into [] (map (fn [x]
                  {:tag *go-component-tag*
                   :attrs {:name (name (first x))}
                   :content (make-game-object-attributes (first x) (second x))})
                obj)))

(defn register-go [obj-key attr-map]
  "在object domain中注册一个concrete object"
  (try (let [obj (make-concrete-game-object obj-key attr-map)]
         (register-in-domain obj-key
                             (keyword (str (get-attribute-value obj :id)))
                             {:tag obj-key
                              :attrs {:id (get-attribute-value obj :id)
                                      :name (get-attribute-value obj :name)}
                              :content (make-game-object-components obj)}))
       (catch Exception e (binding [*out* *err*]
                            (println (format "%s:%d:%s: %s"
                                             (name obj-key)
                                             (:id attr-map)
                                             (:name attr-map)
                                             (.getMessage e)))))))

(defn make-game-object-inspect-table [obj]
  "返回一个符合clojure/inspect-table格式的表"
  (let [comps (keys obj)]
    (reduce into [["属性" "说明" "默认值" "类型" "组件"]]
            (map (fn [x]
                   (apply vector
                          (map (fn [attr]
                                 (let [attr-key (first attr)
                                       attr-value (second attr)
                                       meta-info (get-attribute-meta-info x attr-key #{:doc :type})]
                                   ;; ["属性" "说明" "默认值" "类型" "组件"]
                                   [attr-key (:doc meta-info) attr-value (:type meta-info) x]))
                               (x obj))))
                 comps))))

(defn get-template-attribute-list [temp-key]
  (let [comp-list (keys (make-concrete-template temp-key))]
    (mapcat (fn [x]
              (component-attribute-keys x)) comp-list)))

(defn make-inspect-template [temp-key]
  "返回一个符合clojure/inspect-table格式的表"
  (if-let [obj (make-concrete-template temp-key)]
    (make-game-object-inspect-table obj)))

(defn make-inspect-game-object [domain-key id]
  "返回一个符合clojure/inspect-table格式的表"
  (if-let [obj-node ((keyword (str id)) (get-domain domain-key))]
    (make-game-object-inspect-table (node->concrete-object obj-node :object-node))))
