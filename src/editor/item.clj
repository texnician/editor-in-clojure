(ns editor.item
  (:use (editor template go-template)))

(deffruit :id 1 :name "苹果" :max-own-num 9)
(deffruit :id 2 :name "鸭梨" :max-own-num 99)
(deffruit :id 3 :name "桃子" :max-own-num 999)

(defseed :id 101 :name "苹果种子" :fruit-id 1)
(defseed :id 102 :name "鸭梨种子" :fruit-id 2)
(defseed :id 103 :name "桃树种子" :fruit-id 3 :item-lifetime 7)