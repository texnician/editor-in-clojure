(ns editor.monster
  (:use (editor go-template template)))

(defmonster :id 1 :name "史莱姆1号" :level 3 :hp 300 :mp 800 :species "SLIME"
  :monster-rank "F" :generation 8 :summon-cost 1 :attack 80 :defence 70
  :speed 1 :mental 5 :dodge-rate 2000 :crit-rate 1500 :magic-resistance 8)

(defmonster :id 2 :name "机器人" :level 10 :hp 1000 :mp 1000 :species "METAL" 
  :monster-rank "A" :generation 2 :summon-cost 2 :attack 100 :defence 150
  :speed 4 :mental 6 :dodge-rate 2500 :crit-rate 2000 :magic-resistance 20)

(defmonster :level 6 :id 3 :name "恶魔犬" :hp 500 :mp 500 :species DEMON
  :monster-rank SS :genration 8 :summon-cost 3 :attack 40 :defence 100
  :speed 3 :mental 10 :dodge-rate 2000 :crit-rate 1000 :magic-resistance 70)
