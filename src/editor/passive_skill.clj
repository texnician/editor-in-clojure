(ns editor.monster
  (:use (editor go-template template)))

(defpassive-skill :id 910001 :name "回避上升" :sk-tool-tip "物理攻击回避率2倍"
  ;; :sk-special-info ""
  )

(defpassive-skill :id 910013 :name "连续攻击2次" :sk-tool-tip "物理攻击时必定发生连击（只限单体物理技能）"
  :sk-special-info "每击伤害=正常伤害/攻击次数。会心和打空几率在每击都会进行判定。")