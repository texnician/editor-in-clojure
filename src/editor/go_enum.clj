(ns editor.go-enum
  (:use (editor enum)))

(defenum monster-species-enum
  UNDEFINED-SPECIES                     ;未定义物种
  METAL                                 ;金属类
  SLIME                                 ;史莱姆
  DRAGON                                ;龙
  NATURE                                ;自然
  ORC                                   ;魔兽
  SUBSTANCE                             ;物质
  DEMON                                 ;恶魔
  ZOMBIE                                ;僵尸
  MENG                                  ;萌
  DARK                                  ;黑暗
  UNREAL                                ;幻兽
  )

(defenum monster-rank-enum
  SS
  S
  A
  B
  C
  D
  E
  F)

(defenum magic-resistance-enum
  MR-VULNERABLE
  MR-NORMAL
  MR-MITIGATE-3-4
  MR-MITIGATE-1-2
  MR-MITIGATE-1-4
  MR-IMMUTABLE
  MR-REFLECT
  MR-ABSORB)

(defenum role-class-enum
  DEFAULT-CLASS)

(defenum skill-function-enum
  ATTACK                                ;攻击
  DEFENCE                               ;防御
  REGENERATE                            ;回复
  WEAKEN                                ;弱化
  ASSIST                                ;辅助
  SPECIAL)                              ;特殊

(defenum skill-class-enum
  CURSE                                 ;咒术
  CLEAVE                                ;斩击术
  PHYSICAL                              ;体术
  BREATH                                ;吐息术
  DANCE)                                ;舞技

(defenum skill-property-enum
  UNDEFINED-PROPERTY                    ;默认无属性
  FIRE                                  ;火
  EARTH                                 ;地
  WIND                                  ;风         
  WATER                                 ;水
  THUNDER                               ;圣雷
  DARK                                  ;黑暗
  LIGHT                                 ;光
  THINK                                 ;念
  FIRE-BREATH                           ;火吐息
  FROST-BREATH                          ;冰霜吐息
  PHYSICAL-SEAL                         ;体术封
  CLEAVE                                ;斩术封
  DYING                                 ;濒死
  CONFUSE                               ;幻惑
  POISON                                ;毒药
  MANA-ABSORB                           ;法术吸收
  CURSE-SEAL                            ;咒封
  BREATH-SEAL                           ;吐息封
  DANCE-SEAL                            ;舞技封
  CHAOS                                 ;混乱
  SPIRIT                                ;精神
  PARALYSIS                             ;麻痹
  SLEEP                                 ;沉睡
  ZERO-ATTACK                           ;0攻
  SUNDER-ARMOR                          ;破甲
  SLOW                                  ;缓速
  FOOL                                  ;降智
  ZERO-RESIST                           ;无抗
  )

(defenum skill-range-enum
  SINGLE-ENEMY                          ;敌方单体
  ALL-ENEMY                             ;敌方全体
  SELF                                  ;自己
  SINGLE-FRIEND                         ;友方单体
  ALL-FRIEND)                           ;友方全体

(defenum ai-config
  OFFENSIVE ;全力出击
  NORMAL_ATTACK ;普通攻击
  STRATEGY ;战术攻击
  DEFENSIVE ;保命优先
  )