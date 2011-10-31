(ns editor.monster
  (:use (editor go-template template)))

;; 属性	                    说明	             默认值	        类型	组件
;; :id	                    Domain内的唯一id	     0	            int	    :base
;; :name	                Object的名字	     未命名的技能组	string	:base
;; :skill-group-id-table	技能id表	         []	            int	    :skill-group-data
;; :skill-group-sp-table	技能SP列表，与id表对应 []	            int	    :skill-group-data

(defskill-group :id 1 :name "火焰之风"
  :skill-group-id-table [110001 920059 110009 140001]
  :skill-group-sp-table [     5     10     20     30])

(defskill-group :id 2 :name "水灾"
  :skill-group-id-table [110013 920059 140005]
  :skill-group-sp-table [     5     15     25])