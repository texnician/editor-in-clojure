(ns editor.monster
  (:use (editor go-template template)))

;;; 怪物种族是几个固定值
;  UNDEFINED-SPECIES                     未定义物种(默认)
;  METAL                                 金属类
;  SLIME                                 史莱姆
;  DRAGON                                龙
;  NATURE                                自然
;  ORC                                   魔兽
;  SUBSTANCE                             物质
;  DEMON                                 恶魔
;  ZOMBIE                                僵尸
;  MENG                                  萌
;  DARK                                  黑暗
;  UNREAL                                幻兽

             ;; id  名字           种族            RANK            cost          捕获固有属性
(defmonster :id 11 :name "史莱姆" :species SLIME :monster-rank F :summon-cost 1 :capture-inherent 40
  ;; 火       地           风          水           圣雷          黑暗        光          年
  :mr-fire 1  :mr-earth 1 :mr-wind 1  :mr-water 1 :mr-thunder 3 :mr-dark 0 :mr-light 1 :mr-year 1
  ;; 火吐息          冰霜吐息            体术封               斩术          濒死        幻惑
  :mr-fire-breath 1 :mr-frost-breath 1 :mr-physical-seal 1 :mr-cleave 1 :mr-dying 1 :mr-confuse 1
  ;; 毒         魔吸              咒封              吐息封             舞技封           混乱
  :mr-poison 1 :mr-mana-absorb 0 :mr-curse-seal 3 :mr-breath-seal 1 :mr-dance-seal 1 :mr-chaos 1
  ;; 精神       麻痹             沉睡        0攻                破甲                缓速       降智        无抗
  :mr-spirit 1 :mr-paralysis 1 :mr-sleep 1 :mr-zero-attack  3 :mr-sunder-armor 1 :mr-slow 0 :mr-fool 1 :mr-zero-resist 1
  ;; hp        mp          攻击            防御              速度           智力
  :max-hp 720 :max-mp 280 :max-attack 680 :max-defence 570 :max-speed 780 :max-mental 500
  ;; hp成长体系                   mp成长体系                   攻击成长体系
  :hp-grow-system [11 12 20 15] :mp-grow-system [8 7 10 13] :attack-grow-system [10 12 16 13]
  ;; 防御成长体系                      速度成长体系                     智力成长体系                        经验体系
  :defence-grow-system [12 13 17 14] :speed-grow-system [9 12 18 12] :mental-grow-system [11 14 13 11] :exp-gain-system 2
  ;; 技能组id         扩展技能组id            天赋
  :skill-group-id 6 :extend-skill-group [] :talent-group [910003 930029]
  ;; 剑               斧                锤                 矛                 鞭
  :equip-sword false :equip-axe false :equip-hammer false :equip-spear true :equip-whip true
  ;; 爪             杖
  :equip-claw true :equip-staff true)

(defmonster :id 110
  :name "生化霸王龙"
  :species UNDEFINED-SPECIES
  :monster-rank B
  :summon-cost 3
  :mr-fire 3
  :mr-earth 1
  :mr-wind 1
  :mr-water 0
  :mr-thunder 5
  :mr-dark 1
  :mr-light 1
  :mr-year 5
  :mr-fire-breath 1
  :mr-frost-breath 1
  :mr-physical-seal 1
  :mr-cleave 1
  :mr-dying 5
  :mr-confuse 1
  :mr-poison 5
  :mr-mana-absorb 1
  :mr-curse-seal 1
  :mr-breath-seal 0
  :mr-dance-seal 1
  :mr-chaos 5
  :mr-spirit 5
  :mr-paralysis 5
  :mr-sleep 5
  :mr-zero-attack 0
  :mr-sunder-armor 1
  :mr-slow 3
  :mr-fool 1
  :mr-zero-resist 1
  :max-hp 2300
  :max-mp 190
  :max-attack 800
  :max-defence 600
  :max-speed 240
  :max-mental 360
  :hp-grow-system [26 28 28 30]
  :mp-grow-system [3 2 5 4]
  :attack-grow-system [14 17 20 20]
  :defence-grow-system [4 7 18 14]
  :speed-grow-system [4 3 4 2]
  :mental-grow-system [4 4 14 15]
  :exp-gain-system 11
  :skill-group-id 11
  :extend-skill-group [10 5 8 4]
  :talent-group [910009 930029 910013 910088]
  :equip-sword false
  :equip-axe false
  :equip-hammer false
  :equip-spear false
  :equip-whip false
  :equip-claw false
  :equip-staff true)