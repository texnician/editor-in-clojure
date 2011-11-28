(ns editor.go-component
  (:use (editor component go-enum)))

(defcomponent base
  "Game object基本组件"
  (id :type int :default 0 :doc "Domain内的唯一id")
  (unique-id :type uint64 :default 0 :doc "object的唯一id，通过数据库分配")
  (name :type string :default "(unamed object)" :doc "Object的名字"))

(defcomponent role-info
  "角色信息组件"
  (role-accname :type string :default "(unamed account)" :doc "人物所属帐号名")
  (role-class :type int :default 0 :doc "人物职业")
  (role-class-level :type int :default 1 :doc "职业等级")
  (role-title :type int :default 0 :doc "人物称号")
  (guild :type int :default 0 :doc "人物公会")
  (role-logic :type int :default 1 :doc "逻辑")
  (role-reaction :type int :default 1 :doc "反应")
  (role-memory :type int :default 1 :doc "记忆")
  (role-creativity :type int :default 1 :doc "创造力")
  (role-coordination :type int :default 1 :doc "协调")
  (role-watch :type int :default 1 :doc "观察")
  (role-skin-color :type int :default 0 :doc "肤色")
  (role-face-style :type int :default 0 :doc "脸型")
  (bag-items :type int*8 :default [] :doc "包裹中物品")
  (player-avataritems :type int*8 :default [] :doc "装扮物品")
  (default-player-avataritems :type int*8 :default [] :doc "装备的装扮物品")
  (card-list :type int*8 :default [] :doc "卡片列表")
  (card-level-list :type int*8 :default [] :doc "卡片列表的等级")
  (tree-type :type int*8 :default [] :doc "树的类型")
  (tree-colIndex :type int*8 :default [] :doc "树的x坐标")
  (tree-rowIndex :type int*8 :default [] :doc "树的y坐标")
  (tree-state :type int*8 :default [] :doc "树的状态")
  (tree-growdays :type int*8 :default [] :doc "树已经生长的天数")
  (flower-type :type int*8 :default [] :doc "花的类型")
  (flower-colIndex :type int*8 :default [] :doc "花的x坐标")
  (flower-rowIndex :type int*8 :default [] :doc "花的y坐标")
  (flower-state :type int*8 :default [] :doc "花的状态")
  (flower-growdays :type int*8 :default [] :doc "花已经生长的天数")
  (stone-type :type int*8 :default [] :doc "石头类型")
  (stone-colIndex :type int*8 :default [] :doc "石头的x坐标")
  (stone-rowIndex :type int*8 :default [] :doc "石头的y坐标")
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
  (scene-obj-pos-x :type int :default 0 :doc "元素在场景中的X坐标")
  (scene-obj-pos-y :type int :default 0 :doc "元素在场景中的Y坐标"))

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
  "鱼竿属性组件"
  (fish-bait-nmattadd :type int :default 0 :doc "普通关注加成")
  (fish-bait-rrattadd :type int :default 0 :doc "稀有关注加成"))

(defcomponent fish-pole-property
  "鱼饵属性组件"
  (fish-bdatime :type int :default 0 :doc "鱼咬钩判定增加时间"))

(defcomponent insect-property
  "昆虫属性组件"
  (insect-height :type int*2 :default [5 15] :doc "体长"))

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
  (exp :type int :default 0 :doc "经验;经验满，则等级提升;经验主要在战斗胜利后获得，其它途径也可获得")
  (next-level-exp :type int :default 10 :doc "升级到下一等级所需经验"))

(defcomponent pet-property
  "宠物特有属性"
  (pet-owner :type uint64 :default 0 :doc "宠物所有者id(玩家id)")
  (monster-template-id :type int :default 0 :doc "卡牌兽对应的怪物模板id")
  (polar :type int :default 0 :doc "怪物极性，0表示+, 1表示-")
  (generation :type int :default 0 :doc "RANK后的+X;X >= 0, 当为0时不显示,当为10以上时显示一个ICON;表示该怪物是融合几次后的产物(没融合一次，新生怪RANK后数字+1);若被融合怪物双方的X不同，则取较大的X+1")
  (pet-hometown :type string :default "SNDA Innovations" :doc "家乡")
  (pet-birthday :type string :default "2011/11/15" :doc "生日"))

(defcomponent monster-property
  "怪物专有属性组件"
  (species :type enum :default UNDEFINED-SPECIES :in-domain monster-species-enum
           :doc "物种属性;在战斗中，有专门针对某物种的攻击技能，带来附加伤害。物种属性也与怪物合成有关")
  (monster-rank :type enum :default SS :in-domain monster-rank-enum :doc "RANK;战斗中暂时无用。与怪物属性有关，最明显的怪物强弱标识")
  (summon-cost :type int :default 1 :max 3 :doc "召唤消耗;仅在配置阵容时有用")
  (capture-inherent :type int :default 1 :doc "捕获固有值")
  (mp-height :type string :default "3.1415926" :doc "身高")
  (mp-weight :type string :default "2.7182818" :doc "体重")
  (mp-intro :type string :default "WHO IS YOUR DADDY?" :doc "简介"))

(defcomponent dynamic-combat-property
  "动态战斗属性,需要存档"
  (hp :type int :default 1 :doc "hp;战斗时最重要的可见变量;HP=0时，判定为死亡状态。死亡状态则从战场上移除，失去一切战斗功能")
  (full-hp :type int :default 1 :doc "当前最大hp;")
  (mp :type int :default 1
      :doc "战斗时较重要的可见变量。技能使用都是要通过消耗MP来进行的;MP=0时，或者
      MP<技能需要MP，则不能释放技能;有些攻击会根据MP值来进行共计数值的判定")
  (full-mp :type int :default 1 :doc "当前最大mp;")
  (attack :type int :default 1 :doc "攻击;影响物理攻击与斩击技能")
  (defence :type int :default 1 :doc "防御;影响受到的物理攻击免伤")
  (speed :type int :default 1 :doc "速度;影响行动指令执行时的先后顺序;影响回避率")
  (mental :type int :default 1 :doc "智力;影响魔法攻击效果以及MP")
  (buff-list :type int* :default [] :doc "当前身上的buff list")
  (lineup :type int :default 0 :doc "替补/主力/仓库")
  (lineup-pos :type int :default 0 :doc "阵容中的位置0/1/2/3/4/5")
  (default-ai-config :type enum :in-domain ai-config :default OFFENSIVE :doc "默认AI配置, OFFENSIVE(全力出击), NORMAL_ATTACK (普通攻击), STRATEGY(战术攻击), DEFENSIVE(保命优先)")
  (is-alive :type bool :default true :doc "怪物是否存活")
  (cur-weapon :type int :default 0 :doc "武器， 0表示空手"))

(defcomponent combat-property
  "战斗相关属性组件"
  (mr-fire :type int :default 1 :doc "抗性 火咒")
  (mr-earth :type int :default 1 :doc "抗性 地咒")
  (mr-wind :type int :default 1 :doc "抗性 风咒")
  (mr-water :type int :default 1 :doc "抗性 水冰咒")
  (mr-thunder :type int :default 1 :doc "圣雷")
  (mr-dark :type int :default 1 :doc "黑暗")
  (mr-light :type int :default 1 :doc "光")
  (mr-think :type int :default 1 :doc "念")
  (mr-fire-breath :type int :default 1 :doc "火焰吐息")
  (mr-frost-breath :type int :default 1 :doc "冰霜吐息")
  (mr-physical-seal :type int :default 1 :doc "体术封印")
  (mr-cleave :type int :default 1 :doc "斩术封印")
  (mr-dying :type int :default 1 :doc "濒死")
  (mr-confuse :type int :default 1 :doc "幻惑")
  (mr-poison :type int :default 1 :doc "毒")
  (mr-mana-absorb :type int :default 1 :doc "魔吸")
  (mr-curse-seal :type int :default 1 :doc "咒术封印")
  (mr-breath-seal :type int :default 1 :doc "吐息封")
  (mr-dance-seal :type int :default 1 :doc "舞技封")
  (mr-chaos :type int :default 1 :doc "混乱")
  (mr-spirit :type int :default 1 :doc "精神")
  (mr-paralysis :type int :default 1 :doc "麻痹")
  (mr-sleep :type int :default 1 :doc "睡眠")
  (mr-zero-attack :type int :default 1 :doc "0攻")
  (mr-sunder-armor :type int :default 1 :doc "破甲")
  (mr-slow :type int :default 1 :doc "缓速")
  (mr-fool :type int :default 1 :doc "降智")
  (mr-zero-resist :type int :default 1 :doc "无抗")
  (hp-grow-system :type int* :default [0, 0, 0, 0] :doc "hp成长体系")
  (mp-grow-system :type int* :default [0, 0, 0, 0] :doc "mp成长体系")
  (attack-grow-system :type int* :default [0, 0, 0, 0] :doc "attack成长体系")
  (defence-grow-system :type int* :default [0, 0, 0, 0] :doc "defence成长体系")
  (speed-grow-system :type int* :default [0, 0, 0, 0] :doc "speed成长体系")
  (mental-grow-system :type int* :default [0, 0, 0, 0] :doc "mental成长体系")
  (exp-gain-system :type int :default 0 :doc "经验体系")
  (max-hp :type int :default 1 :doc "Max HP")
  (max-mp :type int :default 1 :doc "Max MP")
  (max-attack :type int :default 1 :doc "最大攻击")
  (max-defence :type int :default 1 :doc "最大防御")
  (max-speed :type int :default 1 :doc "最大速度")
  (max-mental :type int :default 1 :doc "最大智慧"))

(defcomponent skill-caster
  "拥有技能信息组件"
  (skill-group-id :type int :default 0 :doc "技能组id")
  (extend-skill-group :type int* :default [] :doc "扩展技能组id")
  (talent-group :type int* :default [] :doc "天赋id列表"))

(defcomponent weapon-equip
  "装备武器组件"
  (equip-sword :type bool :default false :doc "剑")
  (equip-axe :type bool :default false :doc "斧")
  (equip-hammer :type bool :default false :doc "锤")
  (equip-spear :type bool :default false :doc "矛")
  (equip-whip :type bool :default false :doc "鞭")
  (equip-claw :type bool :default false :doc "爪")
  (equip-staff :type bool :default false :doc "杖"))

(defcomponent skill-group-data
  "技能组信息组件"
  (skill-group-id-table :type int* :default [] :doc "技能id表") 
  (skill-group-sp-table :type int* :default [] :doc "技能SP列表，与id表对应"))

(defcomponent skill-common
  "技能信息组件，主动被动技能都有"
  (sk-tool-tip :type string :default "No Tips" :doc "TIPS")
  (sk-special-info :type string :default "No Info" :doc "特殊参数说明"))

(defcomponent active-skill
  "主动技能组件"
  (sk-property-1 :type enum :in-domain skill-property-enum :default UNDEFINED-PROPERTY :doc "伤害属性1")
  (sk-property-2 :type enum :in-domain skill-property-enum :default UNDEFINED-PROPERTY :doc "伤害属性2")
  (sk-mp :type int :default 0 :doc "MP")
  (sk-range :type enum :in-domain skill-range-enum :default SINGLE-ENEMY :doc "技能范围")
  (sk-crtical :type int :default 0 :doc "暴击")
  (sk-max-dmg :type int :default 0 :doc "最大伤害")
  (sk-base-dmg-lower :type int :default 0 :doc "基础伤害下限")
  (sk-base-dmg-upper :type int :default 0 :doc "基础伤害上限")
  (sk-int-lower :type int :default 0 :doc "智力加成下限")
  (sk-int-upper :type int :default 0 :doc "智力加成上限")
  (sk-final-dmg-lower :type int :default 0 :doc "最终基础伤害下限")
  (sk-final-dmg-upper :type int :default 0 :doc "最终基础伤害上限")
  (sk-attack-multi-lower :type int :default 0 :doc "伤害加成倍数,添整数，万分比，比如11000 = 1.1")
  (sk-attack-multi-upper :type int :default 0 :doc "伤害加成倍数,添整数，万分比，比如11000 = 1.1")
  (sk-species-multi-lower :type int :default 0 :doc "怪物物种加成下限，万分比")
  (sk-species-multi-upper :type int :default 0 :doc "怪物物种加成上限，万分比")
  (sk-ai-config-table :type int* :default [100, 100, 100, 100] :doc "AI配置"))
