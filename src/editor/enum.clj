(ns editor.enum
  (:use (editor domain)))

(defenum asset-type-enum
  "Asset类型枚举"
  UNDEFINED
  NPC
  PLAYER
  PLANTS)

(contains? (into #{} (map #(-> % :attrs :name) (seq (((global-enum-domain) :asset-type-enum) :content)))) "LANTS")
(register-global-enum :asset-type-enum {:tag :global-enum :attrs {:name "asset-type-enum" :doc "Asset类型枚举"}
                                        :content [{:tag :item :attrs {:name "UNDEFINED"} :content ["0"]}
                                                  {:tag :item :attrs {:name "NPC"} :content ["1"]}
                                                  {:tag :item :attrs {:name "PLAYER"} :content ["2"]}
                                                  {:tag :item :attrs {:name "PLANTS"} :content ["3"]}]})

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
  (register-global-enum ))
