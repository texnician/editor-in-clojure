(ns editor.go-template
  (:use (editor template)))

(deftemplate fruit
  "��ʵ��ģ��"
  (base :name "δ�����Ĺ�ʵ")
  (item-base :item-lifetime 10)
  (trade))

(deftemplate seed
  "���ӵ�ģ��"
  (base :name "δ����������")
  (item-base :item-lifetime 3)
  (seeding))

(deffruit :id 1 :name "ƻ��" :max-own-num 99)

{:tag :go-template,
 :attrs {:name "fruit", :doc "��ʵ��ģ��"},
 :content [{:tag :go-component,
            :attrs {:name "base", :doc "Game object�������"},
            :content [{:tag :id,
                       :attrs {:type "int", :default 0, :doc "Domain�ڵ�Ψһid"},
                       :content ["0"]}
                      {:tag :name,
                       :attrs {:type "string", :default "(unamed object)", :doc "Object������"},
                       :content ["δ�����Ĺ�ʵ"]}]}
           {:tag :go-component,
            :attrs {:name "item-base", :doc "��Ʒ�Ļ�������"},
            :content [{:tag :max-own-num,
                       :attrs {:type "int", :default 99999, :doc "��Ʒ������������"},
                       :content ["99999"]}
                      {:tag :item-lifetime,
                       :attrs {:type "int", :default 0, :doc "��Ʒ�Ĵ���ʱ�䣬0��ʾ������"},
                       :content ["10"]}]}
           ]}

{:base {:id ""}
 :item-base {max-own-num ""}}
[{:tag :go-component,
  :attrs {:name "base"},
  :content [{:tag :id,
             :content ["0"]}
            {:tag :name,
             :content ["δ�����Ĺ�ʵ"]}]}
 {:tag :go-component,
  :attrs {:name "item-base"},
  :content [{:tag :max-own-num,
             :content ["99999"]}
            {:tag :item-lifetime,
             :content ["10"]}]}
 ]

{:tag :fruit
 :attrs {:id 2 :name "Ѽ��"}
 :content [{:tag :go-component,
            :attrs {:name "base"},
            :content [{:tag :id,
                       :content ["2"]}
                      {:tag :name,
                       :content ["Ѽ��"]}]}
           {:tag :go-component,
            :attrs {:name "item-base"},
            :content [{:tag :max-own-num,
                       :content ["99"]}
                      {:tag :item-lifetime,
                       :content ["0"]}]}]}

(deffruit :id 2 :name "Ѽ��" :max-own-num 99)
