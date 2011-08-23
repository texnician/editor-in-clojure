(ns editor.component
  (:use (editor core))
  (:use [clojure.xml :as xml]))


(defdomain go-commponent
  (defcomponent object-base
  "GameObject基本组件"
  (id :type int
      :default 0
      :doc "object在游戏运行时唯一的id"
      :runtime-only true)
  (asset-type :type int
              :default 0
              :doc "object的asset类型"
              :constraint (in-enum :UNDEFINED :NPC :PLAYER :ITEM :PLANT))
  (asset-id :type int
            :default 0
            :doc "object在所属asset_type集合中的id,在所属asset_type集合中唯一")
  (display-name :type string
                :default "Base Object"
                :doc "Object的名字"))
  )

(defcomponent random-drop-item
  "物品采集属性组件"
  (item-table :type drop-info
              :default []
              :doc "掉落物品列表"))

(deftemplate tree
  (object-base :asset-type PLANTS)
  (random-drop-item))

(deftree
  :id 1
  :name "梨树"
  :item-table [(drop-info 1001 1000)
               (drop-info 1002 3000)
               (drop-info 1003 5000)
               (drop-info 1004 1000)])

(defenum asset-type-enum UNDEFINED NPC PLAYER PLANT)

(into [] (vals (go-component-domain)))
(editor.domain/go-component-domain->xml)
(macroexpand '(defcomponent object-base
  "GameObject基本组件"
  (id :type int
      :default 0
      :doc "object在游戏运行时唯一的id"
      :runtime-only true)
  (asset-type :type int
              :in asset-type-enum
              :default NPC
              :doc "object的asset类型")
  (asset-id :type int
            :default 0
            :doc "object在所属asset_type集合中的id,在所属asset_type集合中唯一")
  (display-name :type string
                :default "Base Object"
                :doc "Object的名字")))
(defattribute id
  :type int
  :default 0
  :doc "object在游戏运行时唯一的id"
  :runtime-only true)

(deftemplate npc
  "NPC模板"
  (object-base :category NPC)
  (placement))

(deftemplate tree
  "果树模板"
  (object-base :category TREE)
  (placement))

(deftemplate player
  "玩家模板"
  (object-base :category PLAYER)
  (placement))

(defnpc
  (object-base :asset-id 10 :display-name "村长")
  (arts :texture "arts/npc/master_01.png"
        :portrait "arts/npc/master_02.png"
        :animation "arts/npc/master_03.gif"))

(defnpc (object-base :asset-id 20 :display-name "新手引导"
                     :pic-id "act/xx/.png"
                     :))
(defnpc (object-base :asset-id 30 :display-name "家园管理员")
  (:drop-table [:item 10 :rate 5000
                :item 12 :rate 3000
                :item 13 :rate 1000
                :item 14]))

(deftree (object-base :asset-id 10 :name "苹果树"))
(deftree
  (object-base :asset-id 11 :name "梨树")
  (placement :x 10 :y 10 :orient "WEST"))
(deftree (object-base :asset-id 12 :name "橘子树"))
(defachivement-branch gather-branch
  (achivement-branch-base :id 1
                          :rule [])
  (gather-rule ) )

(defachivement-rule :id 1 :doc "搜集" :icon 1 :num 10 :target "小精灵")
(defgather-branch
  :id 1
  :rules "1|2|3|4|5|6"
  :rules [1 2 3 4 5 6]
  :rules [(defachivement-rule :id 1 :doc "搜集" :icon 1 :num 10 :target "小精灵")
          (defachivement-rule :id 2 :doc "搜集" :icon 1 :num 10 :target "小精灵")
          (defachivement-rule :id 3 :doc "搜集" :icon 1 :num 10 :target "小精灵")
          (defachivement-rule :id 4 :doc "搜集" :icon 1 :num 10 :target "小精灵")
          (defachivement-rule :id 5 :doc "搜集" :icon 1 :num 10 :target "小精灵")
          (defachivement-rule :id 6 :doc "搜集" :icon 1 :num 10 :target "小精灵")])