(ns editor.component
  (:use (editor domain)))

(register-go-component :object-base
                       {:tag :go-component-meta
                        :attrs {:name "object-base" :comment "GameObject�������"}
                        :content [{:tag :go-attribute
                                   :attrs {:name "id",
                                           :type "int",
                                           :default 0,
                                           :doc "object����Ϸ����ʱΨһ��id"
                                           :runtime-only "true"}}
                                  {:tag :go-attribute
                                   :attrs {:name "asset-type"
                                           :type "int"
                                           :default 0
                                           :doc "object��asset����"
                                           :constraint [:NPC :ITEM :PLANT]}}
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

(declare build-component-body)

(defmacro defcomponent [name & body]
  `(register-go-component ~(keyword name)
                          {:tag :go-component
                           :attrs {:name ~(str name)
                                   :doc ~(if (string? (first body))
                                           (first body) "No doc")}
                           :content (build-attribute-list '~(rest body))}))

(defn- fix-attribute-meta-data [x]
  (if-let [[key v] x]
    (cond (= :type key) [key (str v)]
          :else [key v])))

(defn- build-attribute [attr]
  {:tag :go-attribute
   :attrs (into {} (map fix-attribute-meta-data
                        (apply array-map (list* :name (-> attr first str) (rest attr)))))})

(defn build-attribute-list [attr-list]
  (loop [acc [] l attr-list]
    (if (empty? l)
      acc
      (recur (conj acc (build-attribute (first l))) (rest l)))))
