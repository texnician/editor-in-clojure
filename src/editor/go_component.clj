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
