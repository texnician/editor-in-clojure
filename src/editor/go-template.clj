(ns editor.go-template
  (:use (editor template))
  (:use (editor component))
  (:use (editor go-component)))

(deftemplate fruit
  "果实的模板"
  (base :name "未命名的果实")
  (item-base :item-lifetime 10)
  (trade))

(deftemplate seed
  "种子的模板"
  (base :name "未命名的种子")
  (item-base :item-lifetime 3)
  (seeding))

(deffruit :id 1 :name "苹果" :max-own-num 99)

(def *sample-template* {:tag :go-template,
 :attrs {:name "fruit", :doc "果实的模板"},
 :content [{:tag :go-component,
            :attrs {:name "base", :doc "Game object基本组件"},
            :content [{:tag :id,
                       :attrs {:type "int", :default 0, :doc "Domain内的唯一id"},
                       :content ["0"]}
                      {:tag :name,
                       :attrs {:type "string", :default "(unamed object)", :doc "Object的名字"},
                       :content ["未命名的果实"]}]}
           {:tag :go-component,
            :attrs {:name "item-base", :doc "物品的基本属性"},
            :content [{:tag :max-own-num,
                       :attrs {:type "int", :default 99999, :doc "物品的最大持有数量"},
                       :content ["99999"]}
                      {:tag :item-lifetime,
                       :attrs {:type "int", :default 0, :doc "物品的存在时间，0表示无限制"},
                       :content ["10"]}]}
           ]})
(defn- extract-attribute-key [node]
  (:tag node))

(defn- extract-attribute-value [node]
  (-> node :content first))

(defn- extract-attribute-list [attr-nodes]
  (into {} (map (fn [c]
                [(extract-attribute-key c) (extract-attribute-value c)]) attr-nodes)))

(defn make-concrete-template [template-node]
  (into {} (map (fn [x]
                  [(-> x :attrs :name keyword)
                   (if-let [content (:content x)]
                     (extract-attribute-list content))]) (:content template-node))))

(def *sample-concrete-template* (make-concrete-template *sample-template*))

(defn find-attribute-component [attr-key comps]
  (some (fn [x]
          (if (contains? (set (component-attribute-keys x)) attr-key)
            x)) comps))

(defn assoc-concrete-attribute-value [concrete-template attr-key value]
  (let [comp-key (find-attribute-component attr-key (keys concrete-template))
        comp-attrs (concrete-template comp-key)]
    (assoc concrete-template comp-key (assoc comp-attrs attr-key value))))

{:base {:id ""}
 :item-base {:max-own-num ""}}

[{:tag :go-component,
  :attrs {:name "base"},
  :content [{:tag :id,
             :content ["0"]}
            {:tag :name,
             :content ["未命名的果实"]}]}
 {:tag :go-component,
  :attrs {:name "item-base"},
  :content [{:tag :max-own-num,
             :content ["99999"]}
            {:tag :item-lifetime,
             :content ["10"]}]}
 ]

{:tag :fruit
 :attrs {:id 2 :name "鸭梨"}
 :content [{:tag :go-component,
            :attrs {:name "base"},
            :content [{:tag :id,
                       :content ["2"]}
                      {:tag :name,
                       :content ["鸭梨"]}]}
           {:tag :go-component,
            :attrs {:name "item-base"},
            :content [{:tag :max-own-num,
                       :content ["99"]}
                      {:tag :item-lifetime,
                       :content ["0"]}]}]}

(deffruit :id 2 :name "鸭梨" :max-own-num 99)
