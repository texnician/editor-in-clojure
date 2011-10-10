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