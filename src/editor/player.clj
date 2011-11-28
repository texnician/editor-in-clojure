(ns editor.monster
  (:use (editor go-template template)))

(defplayer 
:id 1 
:name "初生玩家模板" 
:role-class 1 
:role-class-level 1 
:role-title 1 
:guild 0 
:role-logic 100
:role-reaction 100
:role-memory 100
:role-creativity 100
:role-coordination 100
:role-watch 100
:currency-gold 1000
:bag-items [1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 100001, 100001, 100002, 100002, 100003, 100003, 100010, 100010, 200001, 200001, 200002, 200002, 200003, 200003, 200004, 200004, 200005, 200005, 200006, 200006 ]
:player-avataritems  [90001, 90002, 80001, 80002, 70001, 70002 ]
:default-player-avataritems  [90001, 80001, 70001, ]
:card-list [8, 14]
:card-level-list [10, 12]
;tree-type 1苹果树 2梨树 3橘子树 4椰子树
:tree-type [4, 4, 1, 3, 2, 3]
:tree-colIndex [10, 12, 14, 25, 27, 29]
:tree-rowIndex [26, 26, 16, 20, 20, 20]
;tree-state 1小树苗 2小树 3大树 4结果树 5枯死树
:tree-state [3, 3, 4, 3, 3, 4]
:tree-growdays [5 5 5 5 5 5]
;flower-type 101黄郁金香 102白郁金香 103红郁金香 104粉郁金香 105蓝郁金香 106紫色郁金香
:flower-type [101, 102, 103, 104, 101, 101]
:flower-colIndex [40, 40, 42, 42, 44, 44]
:flower-rowIndex [20, 22, 20, 22, 20, 22]
;flower-state 1花苗 2花苞 3开花 4枯死
:flower-state [3, 3, 3, 3, 2, 2]
:tree-growdays [10, 11, 5, 10, 11, 9 ]
:stone-type [999 999 999]
:stone-colIndex [20, 45, 48 ]
:stone-rowIndex [11, 27, 17  ]

)
