(ns editor.item
  (:use (editor template go-template)))

(deffruit :id 1 :name "苹果" :max-own-num 9 :is-gift false)
(deffruit :id 2 :name "鸭梨" :max-own-num 99)
(deffruit :id 3 :name "桃子" :max-own-num 999 :is-tradable false)
(deffruit :id 4 :name "无花果" :max-own-num 10 :item-lifetime 3)

(defseed :id 101 :name "苹果种子" :fruit-id 1)
(defseed :id 102 :name "鸭梨种子" :fruit-id 2)
(defseed :id 103 :name "桃树种子" :fruit-id 3 :item-lifetime 7)
(defseed :id 104 :name "铁树种子" :fruit-id 0 :item-lifetime 100 :max-own-num 1)
(defseed :id 105 :name "西瓜种子" :item-lifetime 8 :max-own-num 100)

(defcomposite-rule "机器人" (+ "树妖" "史莱姆" "苹果"))

(defcomposite-rule "超级史莱姆" (* "史莱姆" 3))
(defcard :id 30001 :name "魔兽卡" )