(ns editor.component
  (:use (editor domain enum))
  (:require [clojure.string :as string]))

(def *go-component-tag* :go-component)

(defmacro defcomponent [name & body]
  `(register-in-domain :meta-go-component ~(keyword name)
                       {:tag :meta-go-component
                        :attrs {:name ~(str name)
                                :doc ~(if (string? (first body))
                                        (first body) "No doc")}
                        :content (build-attribute-list '~(rest body))}))


(defn- array-info [s]
  (if-let [[_, t, v] (re-matches #"(.*)\*(.*)" s)]
    [t v]
    nil))

(defmulti get-attribute-meta-data (fn [attr-map]
                                    (if-let [type-str (-> attr-map :type str)]
                                      (cond (array-info type-str) :array-type
                                            :else :atom-type))))

(defmethod get-attribute-meta-data :atom-type [attr-map]
  (fn [x]
    (if-let [[key v] x]
      (cond (= :type key) (list [key (str v)]) 
            :else (list [key v])))))

(defmacro eval-sym-str [str]
  (let [sym (symbol str)]
    ``~sym))

(defmethod get-attribute-meta-data :array-type [attr-map]
  (letfn [(array-size [s]
            (cond (= "" s) 0
                  (integer? (read-string s)) (read-string s)
                  :else (eval (symbol s))))]
    (fn [x]
      (if-let [[key v] x]
        (cond (= :type key) (if-let [[t s] (array-info (str v))]
                              (list [:type t]
                                    [:size (array-size s)]))
              (= :default key) (if (vector? v) (list [key v]) 
                                   (list [key (vec (repeat (-> attr-map :type str array-info second array-size) v))]))
              :else (list [key v]))))))

(defn- build-attribute [attr]
  (let [attr-map (apply array-map (list* :name (-> attr first str) (rest attr)))
        f (get-attribute-meta-data attr-map)]
    {:tag :go-attribute
     :attrs (into {} (mapcat f
                             attr-map))}))

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

(def *enum-int-value-tag* :enum-int-value)

(defn attribute-extend-attrs [attr-key attrs attr-val]
  (cond (= "enum" (:type attrs)) {*enum-int-value-tag*
                                  (let [v (enum-int-value (keyword (:in-domain attrs))
                                                                    (keyword attr-val))]
                                    (if (integer? v) v
                                        (do (println attr-key attrs attr-val v) "INVALID-ENUM"))
                                    )}
        :else nil))

(defn make-component-node [comp-key & attr-vals]
  "根据component name 生成一个component节点"
  (if-let [comp-meta (make-meta-comp comp-key)]
    (let [meta-attrs (comp-meta :attributes)]
      {:tag *go-component-tag*
       :attrs {:name (comp-meta :name) :doc (comp-meta :doc)}
       :content (apply vector (map (fn [x]
                                     (let [[attr-key attrs] x
                                           attr-val (if-let [v (attr-key (peek attr-vals))]
                                                      v
                                                      (attrs :default))]
                                       {:tag attr-key
                                        :attrs attrs 
                                        :content
                                        [(str attr-val)]}))
                                   meta-attrs))})))

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

(defn get-attribute-type [comp-key attr-key]
  "返回一个attribute的类型"
  (let [type-info (get-attribute-meta-info comp-key attr-key #{:type})]
    (keyword (:type type-info))))

(defn get-attribute-default-value [comp-key attr-key]
  "返回一个attribute的default value"
  (let [type-info (get-attribute-meta-info comp-key attr-key #{:default})]
    (str (:default type-info))))

(defn get-attribute-in-domain [comp-key attr-key]
  (let [type-info (get-attribute-meta-info comp-key attr-key #{:in-domain})]
    (keyword (:in-domain type-info))))

(defn atom-attribute? [comp-key attr-key]
  (let [attr-info (get-attribute-meta-info comp-key attr-key #{:size})]
    (not (:size attr-info))))

(defn filter-component-attributes [pred comp]
  "返回一个component中符合pred的所有attribute"
  (let [attr-list (component-attribute-keys comp)]
    (filter pred attr-list)))

;; Local Variables:
;; coding: utf-8
;; End:
