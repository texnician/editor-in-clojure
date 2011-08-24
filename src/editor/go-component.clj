(ns editor.go-component
  (:use (editor component)))

(defcomponent base
  "Game object�������"
  (id :type int :default 0 :doc "Domain�ڵ�Ψһid")
  (name :type string :default "(unamed object)" :doc "Object������"))

(defcomponent item-base
  "��Ʒ�Ļ�������"
  (max-own-num :type int :default 99999 :doc "��Ʒ������������")
  (item-lifetime :type int :default 0 :doc "��Ʒ�Ĵ���ʱ�䣬0��ʾ������"))

(defcomponent seeding
  "�����������"
  (fruit-id :type int :default 0 :doc "��ʵid" :reference fruits-domain))

(defcomponent trade
  "������ص����"
  (is-tradable :type bool :default true :doc "�Ƿ���Խ���")
  (buy-price :type int :default 0 :doc "����۸�")
  (sell-price :type int :default 0 :doc "���ռ۸�")
  (repair-price :type int :default 0 :doc "����۸�")
  (is-gift :type bool :default true :doc "�Ƿ��������"))

(defcomponent vip-item
  "VIP��Ʒ������"
  (vip-level :type int :default 0 :doc "VIP�޹��ȼ�")
  (vip-only :type bool :default false :doc "�Ƿ�VIPר��"))
