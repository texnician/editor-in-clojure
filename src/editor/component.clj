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

;; [{:tag :go-attribute, :attrs {:name "id", :type "int", :default 0, :doc "Domain�ڵ�Ψһid"}}
;;              {:tag :go-attribute, :attrs {:name "name", :type "string", :default "(unamed object)", :doc "Object������"}}]

;; {:id {:type "int"
;;       :default 0
;;       :doc "Domain�ڵ�Ψһid"}
;;  :name {:type "string"
;;         :default "(unamed object)"
;;         :doc "Object������"}}

(defn make-meta-attributes [meta-attribute-node]
  "����meta-attribute-node����meta-attributes"
  (if-let [attr-maps (map :attrs meta-attribute-node)]
    (into {} (map (fn [entry]
                    [(keyword (entry :name)) (dissoc entry :name)])
                  attr-maps))))

;; {:name "base"
;;  :doc "Object������"
;;  :attributes {:id {:type "int"
;;                    :default 0
;;                    :doc "Domain�ڵ�Ψһid"}
;;               :name {:type "string"
;;                      :default "(unamed object)"
;;                      :doc "Object������"}}}

(defn make-meta-comp [meta-node]
  "����go-component-domain�е�component node����һ��meta component"
  (assoc (meta-node :attrs) :attributes (make-meta-attributes (meta-node :content))))

;; {:content [{:tag :go-attribute, :attrs {:name "id", :type "int", :default 0, :doc "Domain�ڵ�Ψһid"}} {:tag :go-attribute, :attrs {:name "name", :type "string", :default "(unamed object)", :doc "Object������"}}], :attrs {:name "base", :doc "Game object�������"}, :tag :go-component}

;; {:tag :go-component
;;             :attrs {:name "base" :doc "�������"}
;;             :content [{:tag :id
;;                        :attrs {:type "int" :doc "Domain�ڵ�Ψһid"}
;;                        :content ["0"]}
;;                       {:tag :name
;;                        :attrs {:type "string" :doc "Object������"}
;;                        :content ["(Unamed)"]}]}

(defn make-component-node [comp-key & attr-vals]
  "����component name ����һ��component�ڵ�"
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