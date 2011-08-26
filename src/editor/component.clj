(ns editor.component
  (:use (editor domain)))

(defmacro defcomponent [name & body]
  `(register-in-domain :meta-go-component ~(keyword name)
                       {:tag :meta-go-component
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

;; [{:tag :go-attribute, :attrs {:name "id", :type "int", :default 0, :doc "Domain内的唯一id"}}
;;              {:tag :go-attribute, :attrs {:name "name", :type "string", :default "(unamed object)", :doc "Object的名字"}}]

;; {:id {:type "int"
;;       :default 0
;;       :doc "Domain内的唯一id"}
;;  :name {:type "string"
;;         :default "(unamed object)"
;;         :doc "Object的名字"}}

(defn make-meta-attributes [meta-attribute-node]
  "根据meta-attribute-node生成meta-attributes"
  (if-let [attr-maps (map :attrs meta-attribute-node)]
    (into {} (map (fn [entry]
                    [(keyword (entry :name)) (dissoc entry :name)])
                  attr-maps))))

;; {:name "base"
;;  :doc "Object的名字"
;;  :attributes {:id {:type "int"
;;                    :default 0
;;                    :doc "Domain内的唯一id"}
;;               :name {:type "string"
;;                      :default "(unamed object)"
;;                      :doc "Object的名字"}}}

(defn make-meta-comp [comp-key]
  "根据go-component-domain中的component key生成一个meta component"
  (if-let [meta-node ((get-domain :meta-go-component) comp-key)]
    (assoc (meta-node :attrs) :attributes (make-meta-attributes (meta-node :content)))))

(defn component-attribute-keys [comp-key]
  "返回一个component所有的attribute key"
  (-> comp-key make-meta-comp :attributes keys))

;; {:content [{:tag :go-attribute, :attrs {:name "id", :type "int", :default 0, :doc "Domain内的唯一id"}} {:tag :go-attribute, :attrs {:name "name", :type "string", :default "(unamed object)", :doc "Object的名字"}}], :attrs {:name "base", :doc "Game object基本组件"}, :tag :go-component}

;; {:tag :go-component
;;             :attrs {:name "base" :doc "基本组件"}
;;             :content [{:tag :id
;;                        :attrs {:type "int" :doc "Domain内的唯一id"}
;;                        :content ["0"]}
;;                       {:tag :name
;;                        :attrs {:type "string" :doc "Object的名字"}
;;                        :content ["(Unamed)"]}]}

(defn make-component-node [comp-key & attr-vals]
  "根据component name 生成一个component节点"
  (if-let [comp-meta (make-meta-comp comp-key)]
    (let [meta-attrs (comp-meta :attributes)]
      {:tag :go-component
       :attrs {:name (comp-meta :name) :doc (comp-meta :doc)}
       :content (apply vector (map (fn [x]
                                     {:tag (first x)
                                      :attrs (second x)
                                      :content
                                      (if-let [v ((first x) (peek attr-vals))]
                                        [(str v)]
                                        (if-let [default ((second x) :default)]
                                          [(str default)]
                                          nil))
                                      }) meta-attrs))})))

(defn get-attribute-meta-info [comp-key attr-key attr-key-set]
  "返回attribute的meta info, :doc :type 等等, attr-key-set为要查询的key set, 例如#{:doc :type}"
  (if-let [comp-meta (make-meta-comp comp-key)]
    (let [attributes (comp-meta :attributes)]
      (into {} (filter #(-> % second nil? not) (map (fn [x]
                                         [x (-> attributes attr-key x)]) attr-key-set))))))

(defn make-inspect-table-rows [comp-key]
  "返回适用于clojure/inspect-table格式的表"
  (if-let [comp-meta (make-meta-comp comp-key)]
    (let [comp-name (comp-meta :name)
          attributes (comp-meta :attributes)]
      (apply vector (map (fn [x]
                           (if-let [meta-attr (second x)]
                             [(-> x first name) (:doc meta-attr) (:default meta-attr) (:type meta-attr)  comp-name]))
                         attributes)))))

;; Local Variables:
;; coding: utf-8
;; End:
