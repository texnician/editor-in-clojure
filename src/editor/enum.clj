(ns editor.enum
  (:use (editor domain)))

(declare fn-global-enum-contains?)

(defmacro global-enum-contains? [enum sym]
  "Test if enum contains sym."
  `(fn-global-enum-contains? ~(keyword enum) ~(str sym)))

(defn- fn-global-enum-contains? [enum sym]
  (let [enum-symbol-set (into #{} (map #(-> % :attrs :name) (:content ((global-enum-domain) enum))))]
    (contains? enum-symbol-set  sym)))

(declare fn-global-enum-map)

(defmacro global-enum-map [enum]
  "Get enum [key value] map."
  `(fn-global-enum-map ~(keyword enum)))

(defn- fn-global-enum-map [enum]
  (into {} (map (fn [x]
                  (let [k (-> x :attrs :name keyword)
                        v (-> x :content first read-string)]
                    [k v])) (:content ((global-enum-domain) enum)))))

(defmacro global-enum-value [enum sym]
  "Get enum value of sym."
  `((global-enum-map ~enum) ~(keyword sym)))

(defmacro defenum [name & body]
  (let [name-str (str name)
        [doc & enums] (if (string? (first body))
                        body
                        (conj body ""))]
    `(register-global-enum ~(keyword name)
                           (build-enum-body ~name-str ~doc ~@(map (fn [x]
                                                                    `'~x) enums)))))

(declare build-enum-items)

(defn- build-enum-body [name doc & enums]
  {:tag :global-enum
   :attrs {:name name :doc doc}
   :content (build-enum-items enums)})

(defn- build-enum-items
  ([enums] (build-enum-items [] 0 enums))
  ([acc i enums]
     (if (empty? enums)
       acc
       (recur (conj acc ((fn [sym n]
                           {:tag :item :attrs {:name (str sym)} :content [(str n)]})
                         (first enums) i))
              (inc i)
              (rest enums)))))

(defenum asset-type-enum
  "Asset类型枚举"
  UNDEFINED
  NPC
  PLAYER
  PLANTS
  ITEMS
  MAPS)

(register-global-enum :asset-type-enum {:tag :global-enum :attrs {:name "asset-type-enum" :doc "Asset类型枚举"}
                                        :content [{:tag :item :attrs {:name "UNDEFINED"} :content ["0"]}
                                                  {:tag :item :attrs {:name "NPC"} :content ["1"]}
                                                  {:tag :item :attrs {:name "PLAYER"} :content ["2"]}
                                                  {:tag :item :attrs {:name "PLANTS"} :content ["3"]}]})