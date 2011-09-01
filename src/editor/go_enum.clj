(ns editor.go-enum
  (:use (editor enum)))

(defenum monster-species-enum
  UNDEFINED-SPECIES                     ;未定义物种
  METAL                                 ;金属类
  SLIME                                 ;史莱姆
  DRAGON                                ;龙
  NATURAL                               ;自然
  ORC                                   ;魔兽
  SUBSTANCE                             ;物质
  DEMON                                 ;恶魔
  ZOMBIE                                ;僵尸
  )