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
           {:tag :go-component,
            :attrs {:name "trade", :doc "������ص����"},
            :content [{:tag :is-tradable,
                       :attrs {:type "bool", :default true, :doc "�Ƿ���Խ���"},
                       :content ["true"]}
                      {:tag :buy-price,
                       :attrs {:type "int", :default 0, :doc "����۸�"},
                       :content ["0"]}
                      {:tag :sell-price,
                       :attrs {:type "int", :default 0, :doc "���ռ۸�"},
                       :content ["0"]}
                      {:tag :repair-price,
                       :attrs {:type "int", :default 0, :doc "����۸�"},
                       :content ["0"]}
                      {:tag :is-gift,
                       :attrs {:type "bool", :default true, :doc "�Ƿ��������"},
                       :content ["true"]}]}]}

(deffruit :id 2 :name "Ѽ��" :max-own-num 99)
