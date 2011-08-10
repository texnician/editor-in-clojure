(ns editor.core)

(defcomponent object-base
  "GameObject�������"
  (id :type int
      :default 0
      :doc "object����Ϸ����ʱΨһ��id"
      :runtime-only true)
  (asset-type :type int
              :default 0
              :doc "object��asset����"
              :constraint asset-type-enum)
  (asset-id :type int
            :default 0
            :doc "object������asset_type�����е�id,������asset_type������Ψһ")
  (display-name :type string
                :default "Base Object"
                :doc "Object������"))

(defmacro defcomponent [name & body]
  `())

(defn register-in-go-component-domain [key c]
  (assoc *go-commponent-domain* key c))

(defn get-go-component [key]
  (key *go-commponent-domain*))

(register-go-component :object-base {:tag :go-component-meta
 :attrs {:name "object-base" :comment "GameObject�������"}
 :content [{:tag :go-attribute
            :attrs {:name "id",
                    :type "int",
                    :default 0,
                    :doc "object����Ϸ����ʱΨһ��id"
                    :runtime-only "true"}}
           {:tag :go-attribute
            :attrs {:name "asset-type"
                    :type enum
                    :default 0
                    :doc "object��asset����"
                    :constraint asset-type-enum}}
           {:tag :go-attribute
            :attrs {:name "asset-id"
                    :type "int"
                    :default 0
                    :doc "object������asset_type�����е�id,������asset_type������Ψһ"}}
           {:tag :go-attribute
            :attrs {:name "display-name"
                    :type "string"
                    :default "Base Object"
                    :doc "Oject������"}}]})


(= (name :object-base) "object-base")

{:tag :go-template
 :attrs {:name "fruit" :comment "��ʵ��ģ��"}
 :content [{:tag :go-component
            :attrs {:id 0 :asset-type :ITEM :asset-id 0 :display-name "����"}}]}

{:tag :go-fruit
 :attrs {:doc "��Ϸ�����й�ʵ������"}
 :content
 [{:tag :go
   :attrs {:doc "ƻ��"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9527 :display-name "ƻ��"}}]}
  {:tag :go
   :attrs {:doc "Ѽ��"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9528 :display-name "Ѽ��"}}]}
  {:tag :go
   :attrs {:doc "Ѽ��"}
   :content
   [{:tag :go-component
     :attrs {:name "object-base" :id 0 :asset-type :FRUIT :asset-id 9528 :display-name "����"}}]}]}


(defmacro deftemplate [name & body]
  `())

( defcomponent)
(meta #'defcomponent)
(ns-unmap 'editor.core 'defcomponent)
(ns-unmap ')