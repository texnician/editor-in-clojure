(ns editor.monster
  (:use (editor go-template template)))

;; ;; 属性	                    说明	              默认值	             类型	组件
;; :id	                        Domain内的唯一id	      0	                     int	:base
;; :object-id	                GameObject运行时id	  0	                     int	:base
;; :name	                    Object的名字	      (unamed object)	     string	:base
;; :sk-tool-tip	                TIPS	              No Tips	             string	:skill-common
;; :sk-special-info	            特殊参数说明	          No Info	             string	:skill-common
;; :sk-base-dmg-upper	        基础伤害上限	          0	                     int	:active-skill
;; :sk-base-dmg-lower	        基础伤害下限	          0	                     int	:active-skill
;; :sk-crtical	                暴击	              0	                     int	:active-skill
;; :sk-int-upper	            智力加成上限	          0	                     int	:active-skill
;; :sk-range	                技能范围	              SINGLE-ENEMY           enum	:active-skill
;; :sk-species-multi-upper	    怪物物种加成上限，万分比 0	                     int	:active-skill
;; :sk-final-dmg-lower	        最终基础伤害下限	      0                      int	:active-skill
;; :sk-species-multi-lower	    怪物物种加成下限，万分比 0	                     int	:active-skill
;; :sk-int-lower	            智力加成下限	          0	                     int	:active-skill
;; :sk-final-dmg-upper	        最终基础伤害上限	      0	                     int	:active-skill
;; :sk-attack-multi-upper	    伤害加成倍数,添整数，万分比，比如11000 = 1.1  0	 int	:active-skill
;; :sk-mp	                    MP	                  0	                     int	:active-skill
;; :sk-max-dmg	                最大伤害	              0	                     int	:active-skill
;; :sk-property-1	            伤害属性1	          UNDEFINED-PROPERTY	 enum	:active-skill
;; :sk-property-2	            伤害属性2	          UNDEFINED-PROPERTY	 enum	:active-skill
;; :sk-attack-multi-lower	    伤害加成倍数,添整数，万分比，比如11000 = 1.1  0	 int	:active-skill

;;; 技能属性说明 :sk-property-1 :sk-property-2
;; UNDEFINED-PROPERTY                    ;默认无属性
;; FIRE                                  ;火
;; EARTH                                 ;地
;; WIND                                  ;风         
;; WATER                                 ;水
;; THUNDER                               ;圣雷
;; DARK                                  ;黑暗
;; LIGHT                                 ;光
;; THINK                                 ;念
;; FIRE-BREATH                           ;火吐息
;; FROST-BREATH                          ;冰霜吐息
;; DYING                                 ;濒死
;; CONFUSE                               ;幻惑
;; POISON                                ;毒药
;; MANA-ABSORB                           ;法术吸收
;; CHAOS                                 ;混乱
;; SPIRIT                                ;精神
;; PARALYSIS                             ;麻痹
;; SLEEP                                 ;沉睡
;; ZERO-ATTACK                           ;0攻
;; SUNDER-ARMOR                          ;破甲
;; SLOW                                  ;缓速
;; FOOL                                  ;降智
;; ZERO-RESIST                           ;无抗

;;; 技能范围说明 :sk-range
;; SINGLE-ENEMY                          ;敌方单体
;; ALL-ENEMY                             ;敌方全体
;; SINGLE-FRIEND                         ;友方单体
;; ALL-FRIEND                            ;友方全体

(defactive-skill :id 110001 :name "火咒术" :sk-property-1 FIRE :sk-mp 2 :sk-range SINGLE-ENEMY
  :sk-crtical 1 :sk-max-dmg 345 :sk-tool-tip "对敌方单体造成魔法伤害14-15点。"
  :sk-base-dmg-lower 14 :sk-base-dmg-upper 15
  :sk-int-lower 50  :sk-int-upper 499
  :sk-final-dmg-lower 109 :sk-final-dmg-upper 120
  ;:sk-attack-multi-lower 10000 :sk-attack-multi-upper 10000
  ;:sk-species-multi-lower 10000 :sk-species-multi-upper 100000
  )

(defactive-skill :id 120005 :name "真空斩" :sk-property-1 WIND :sk-property-2 DARK :sk-mp 2 :sk-range SINGLE-ENEMY
  :sk-crtical 0 :sk-max-dmg 999 :sk-tool-tip "对敌方单体造成物理打击，并给予额外伤害。"
  ;:sk-base-dmg-lower 14 :sk-base-dmg-upper 15
  ;:sk-int-lower 50  :sk-int-upper 499
  ;:sk-final-dmg-lower 109 :sk-final-dmg-upper 120
  ;:sk-attack-multi-lower 10000
  :sk-attack-multi-upper 11000
  ;:sk-species-multi-lower 10000 :sk-species-multi-upper 100000
  )