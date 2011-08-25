(ns editor.component
  (:use (editor domain)))

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

(defn make-meta-comp [meta-node]
  "根据go-component-domain中的component node生成一个meta component"
  (assoc (meta-node :attrs) :attributes (make-meta-attributes (meta-node :content))))

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
  (if-let [comp-meta-node ((go-component-domain) comp-key)]
    (let [comp-meta (make-meta-comp comp-meta-node)
          meta-attrs (comp-meta :attributes)]
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