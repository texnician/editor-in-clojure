(ns editor.go-component
  (:use (editor component)))

(defcomponent base
  "Game object基本组件"
  (id :type int :default 0 :doc "Domain内的唯一id")
  (name :type string :default "(unamed object)" :doc "Object的名字"))

(defcomponent item-base
  "物品的基本属性"
  (max-own-num :type int :default 99999 :doc "物品的最大持有数量")
  (item-lifetime :type int :default 0 :doc "物品的存在时间，0表示无限制"))

(defcomponent seeding
  "种子属性组件"
  (fruit-id :type int :default 0 :doc "果实id" :reference fruits-domain))

(defcomponent trade
  "交易相关的组件"
  (is-tradable :type bool :default true :doc "是否可以交易")
  (buy-price :type int :default 0 :doc "购买价格")
  (sell-price :type int :default 0 :doc "回收价格")
  (repair-price :type int :default 0 :doc "修理价格")
  (is-gift :type bool :default true :doc "是否可以赠送"))

(defcomponent vip-item
  "VIP物品相关组件"
  (vip-level :type int :default 0 :doc "VIP限购等级")
  (vip-only :type bool :default false :doc "是否VIP专属"))

(defcomponent rpg-property
  "RPG游戏通用属性组件"
  (level :type int :default 1 :max 100 :doc "等级;战斗时暂时无用。与技能点习得与属性增加有关")
  (exp :type int :default 0 :doc "经验满，则等级提升;经验主要在战斗胜利后获得，其它途径也可获得")
  (hp :type int :default 1 :doc "战斗时最重要的可见变量;HP=0时，判定为死亡状态。死亡状态则从战场上移除，失去一切战斗功能")
  (mp :type int :default 1
      :doc "战斗时较重要的可见变量。技能使用都是要通过消耗
  MP来进行的;MP=0时，或者MP<技能需要MP，则不能释放技能;有些攻击会根据MP值来进行共
  计数值的判定"))
