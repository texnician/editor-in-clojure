(ns editor.go-template
  (:use (editor template domain))
  (:use (editor go-component)))

(deftemplate fruit
  "果实的模板"
  (base :name "未命名的果实")
  (item-base :item-lifetime 10)
  (trade))

(deftemplate seed
  "种子的模板"
  (base :name "未命名的种子")
  (item-base :item-lifetime 3)
  (seeding))

(deftemplate monster
  "怪物的模板"
  (base :name "未命名的怪物")
  (rpg-property)
  (monster-property)
  (combat-property)
  (skill-caster)
  (weapon-equip))

(deftemplate player
  "玩家的模板"
  (base :name "未命名的玩家")
  (rpg-property)
  (role-info)
  (currency))

(deftemplate pit
  "坑的模板"
  (base :name "未命名的坑")
  (scene-object))

(deftemplate tree
  "树的模板"
  (base :name "未命名的树")
  (scene-object))

(deftemplate skill-group
  "技能组模板"
  (base :name "未命名的技能组")
  (skill-group-data))


(deftemplate passive-skill
  "主动技能"
  (base)
  (skill-common))

(deftemplate active-skill
  "被动技能"
  (base)
  (skill-common)
  (active-skill))