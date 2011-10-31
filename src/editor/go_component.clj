(ns editor.go-component
  (:use (editor component go-enum)))

(defcomponent base
  "Game object基本组件"
  (id :type int :default 0 :doc "Domain内的唯一id")
  (object-id :type int :default 0 :doc "GameObject运行时id")
  (name :type string :default "(unamed object)" :doc "Object的名字"))

(defcomponent role-info
  "角色信息组件"
  (role-class :type enum :default DEFAULT-CLASS :in-domain role-class-enum :doc "人物职业")
  (role-class-level :type int :default 1 :doc "职业等级")
  (role-title :type int :default 0 :doc "人物称号")
  (guild :type int :default 0 :doc "人物公会")
  (role-logic :type int :default 1 :doc "逻辑")
  (role-reaction :type int :default 1 :doc "反应")
  (role-memory :type int :default 1 :doc "记忆")
  (role-creativity :type int :default 1 :doc "创造力")
  (role-love :type int :default 1 :doc "爱心")
  (role-skin-color :type int :default 0 :doc "肤色")
  (role-face-style :type int :default 0 :doc "脸型")
  (home-weather :type int :default 0 :doc "家园天气"))

(defcomponent currency
  "角色货币组件"
  (currency-gold :type int :default 1 :doc "金币"))

(defcomponent item-base
  "物品的基本属性"
  (max-own-num :type int :default 99999 :doc "物品的最大持有数量")
  (item-lifetime :type int :default 0 :doc "物品的存在时间，0表示无限制"))

(defcomponent scene-object
  "场景元素的基本属性"
  (scene-object-position :type int :default 0 :doc "元素在场景中的位置"))

(defcomponent seeding
  "种子属性组件"
  (fruit-id :type int :default 0 :doc "果实id" :reference fruits-domain))

(defcomponent fish-property
  "鱼类属性组件"
  (fish-height :type int :default 0 :doc "体长")
  (fish-water-type :type int :default 1 :doc "淡水/咸水（淡水：1；咸水：2）")
  (fish-buoy :type int*2 :default [1 2] :doc "对应浮漂")
  (fish-attrate :type int :default 6 :doc "关注概率")
  (fish-rare :type int :default 1 :doc "是否稀有（普通：1；稀有：2）")
  (fish-fkbtrate :type int :default 4 :doc "假装咬钩概率")
  (fish-bdtime :type int :default 1000 :doc "咬钩判定时间")
  (fish-action :type int*8 :default [5 5 0 0 0 0 0 0] :doc "行为"))

(defcomponent fish-bait-property
  "鱼饵属性组件"
  (:fish-bait-nmattadd :type int :default 0 :doc "普通关注加成")
  (:fish-bait-rrattadd :type int :default 0 :doc "稀有关注加成"))

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
  (monster-rank :type enum :default SS :in-domain monster-rank-enum :doc "RANK;战斗中暂时无用。与怪物属性有关，最明显的怪物强弱标识")
  (generation :type int :default 0 :doc "RANK后的+X;X >= 0, 当为0时不显示,当为10以上时显示一个ICON;表示该怪物是融合几次后的产物(没融合一次，新生怪RANK后数字+1);若被融合怪物双方的X不同，则取较大的X+1")
  (summon-cost :type int :default 1 :max 3 :doc "召唤消耗;仅在配置阵容时有用")
  (monster-weapon :type int :default 0 :doc "怪物武器的id, 一个怪物可以装备的武器种类有限，并且只能装备1把"))

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
  (hit-rate :type int :default 1 :max 10000
            :doc "怪物命中怪物的几率，与对方回避也有关系")
  (mr-fire-curse :type enum :default MR-NORMAL :in-domain magic-resistance-enum :doc "抗性 火咒")
  (mr-water-curse :type enum :default MR-NORMAL :in-domain magic-resistance-enum :doc "抗性 水冰咒")
  (mr-wind-curse :type enum :default MR-NORMAL :in-domain magic-resistance-enum :doc "抗性 风咒")
  (mr-earth-curse :type enum :default MR-NORMAL :in-domain magic-resistance-enum :doc "抗性 地咒")
  (mr-kira :type enum :default MR-NORMAL :in-domain magic-resistance-enum :doc "基拉")
  (magic-resistance :type int*10 :default [0 0 0 0 0 0 0 0 0 0] :min 0 :max 7 :doc "抗性(魔法防御);28种抗性")
  (mrs :type enum*28 :default [MR-MITIGATE-3-4 MR-MITIGATE-1-2 MR-MITIGATE-1-4 MR-IMMUTABLE MR-REFLECT] :doc "28抗性enum" :in-domain magic-resistance-enum)
  (marks :type string*5 :default ["as" "dk" "wr" "hk" "mm"] :doc "测试属性")
  (long-id :type int64 :default 13818293630 :doc "一个64位的长id")
  (long-id-vec :type int64*10 :default [13818293630 13482865451] :doc "一个64位的长id数组")
  (uint-id :type uint :default 4294967295 :doc "一个32位的uint")
  (uint-id-vec :type uint*8 :default [4294967295 4294967294] :doc "一个32位的uint数组")
  (uint64-id :type uint64 :default 222404198112310414 :doc "一个64位的长uint")
  (uint64-id-vec :type uint64*10 :default [222404198112310414 220204198401140000] :doc "一个64位的长uint数组"))
