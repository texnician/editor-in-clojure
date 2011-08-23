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
