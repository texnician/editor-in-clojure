(ns editor.core)

(defmacro defcomponent [name & body]
  `())

{:tag :go-component-meta
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
            :attrs {:name "asset-id"
                    :type "int"
                    :default 0
                    :doc "object在所属asset_type集合中的id,在所属asset_type集合中唯一"}}
           {:tag :go-attribute
            :attrs {:name "display-name"
                    :type "string"
                    :default "Base Object"
                    :doc "Oject的名字"}}]}

(= (name :object-base) "object-base")

{:tag :go-template
 :attrs {:name "fruit" :comment "果实的模板"}
 :content [{:tag :go-component
            :attrs {:id 0 :asset-type :ITEM :asset-id 0 :display-name "无名"}}]}

{:tag :go-fruit
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
   :attrs {:doc "鸭梨"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9528 :display-name "桃子"}}]}]}


(defmacro deftemplate [name & body]
  `())
