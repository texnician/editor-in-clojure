(ns component)

(defcomponent object-base
  "GameObject基本组件"
  (id :type int
      :default 0
      :doc "object在游戏运行时唯一的id"
      :runtime-only true)
  (asset-type :type int
              :default 0
              :doc "object的asset类型"
              :constraint asset-type-enum)
  (asset-id :type int
            :default 0
            :doc "object在所属asset_type集合中的id,在所属asset_type集合中唯一")
  (display-name :type string
                :default "Base Object"
                :doc "Object的名字"))

(defattribute id
  :type int
  :default 0
  :doc "object在游戏运行时唯一的id"
  :runtime-only true)

(deftemplate npc
  "NPC模板"
  (object-base :asset-type NPC)
  (placement))

(deftemplate tree
  "果树模板"
  (object-base :asset-type TREE)
  (placement))

(defnpc (object-base :asset-id 10 :display-name "村长")
  (arts :texture "arts/npc/master_01.png"
        :portrait "arts/npc/master_02.png"
        :animation "arts/npc/master_03.gif"))

(defnpc (object-base :asset-id 20 :display-name "新手引导"))
(defnpc (object-base :asset-id 30 :display-name "家园管理员"))

(deftree (object-base :asset-id 10 :display-name "苹果树"))
(deftree (object-base :asset-id 11 :display-name "梨树"))
(deftree (object-base :asset-id 12 :display-name "橘子树"))
