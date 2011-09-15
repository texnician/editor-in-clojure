(ns editor.go-component
  (:use (editor component go-enum)))

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
  (exp :type int :default 0 :doc "经验;经验满，则等级提升;经验主要在战斗胜利后获得，其它途径也可获得"))

(defcomponent monster-property
  "怪物专有属性组件"
  (species :type enum :default UNDEFINED-SPECIES :in-domain monster-species-enum
           :doc "物种属性;在战斗中，有专门针对某物种的攻击技能，带来附加伤害。物种属性也与怪物合成有关")
  (monster-rank :type enum :default F :in-domain monster-rank-enum :doc "RANK;战斗中暂时无用。与怪物属性有关，最明显的怪物强弱标识")
  (generation :type int :default 0 :max 10 :doc "怪物的后代数(合成次数)")
  (summon-cost :type int :default 1 :max 3 :doc "召唤消耗;仅在配置阵容时有用"))

(defcomponent combat-property
  "战斗相关属性组件"
  (hp :type int :default 1 :doc "hp;战斗时最重要的可见变量;HP=0时，判定为死亡状态。死亡状态则从战场上移除，失去一切战斗功能")
  (max-hp :type int :default 1 :doc "Max HP")
  (mp :type int :default 1
      :doc "战斗时较重要的可见变量。技能使用都是要通过消耗MP来进行的;MP=0时，或者
      MP<技能需要MP，则不能释放技能;有些攻击会根据MP值来进行共计数值的判定")
  (max-mp :type int :default 1 :doc "Max MP")
  (attack :type int :default 1 :doc "攻击;影响物理攻击与斩击技能")
  (defence :type int :default 1 :doc "防御;影响受到的物理攻击免伤")
  (speed :type int :default 1 :doc "速度;影响行动指令执行时的先后顺序;影响回避率")
  (mental :type int :default 1 :doc "智力;影响魔法攻击效果以及MP")
  (dodge-rate :type int :default 1 :max 10000 :doc "回避率;与速度有关(除了各种技能提升回避率")
  (crit-rate :type int :default 1 :max 10000
             :doc "暴击率;每个怪物都有自己特定的暴击率;物理攻击可享受加成，但魔法
             攻击一部分没有暴击;怪物的特性会影响暴击几率;部分武器可以提高暴击
             率")
  (magic-resistance :type int :default 0 :min 0 :max 7 :doc "抗性(魔法防御);28种抗性"))
