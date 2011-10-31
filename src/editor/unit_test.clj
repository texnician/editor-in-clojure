(ns editor.unit-test
  (:use (editor domain template name-util core types go-enum go-component sid))
  (:use [clojure.xml :as xml])
  (:require [clojure.string :as string]))

(defmacro deffactory-test [comp-key & attr-map]
  "定义一个component的test case"
  (let [comp-sym (symbol (name comp-key))
        attrs (merge {:id 1 :name "TestObject"} (first attr-map))]
    `(with-test-domain (get-sub-domain :meta-go-component :global-enum)
       (deftemplate ~'test-obj ~@(if (= 'base comp-sym)
                                   (list (list 'base))
                                   (list (list 'base) (list comp-sym))))
       (deftest-obj-fn ~@(mapcat (fn [[k# v#]]
                                   (list k# v#)) attrs))
       (component-factory-test-case ~(keyword 'test-obj) ~comp-key ~(-> attrs :id str keyword)))))

(defn- make-atom-attribute-test-statement [comp-key attr-key val]
  (let [attr-info (make-cpp-attribute comp-key attr-key)]
    ((:assert-eq attr-info) val)))


(defn component-factory-test-case [domain comp-key id-key]
  (let [xml-node (id-key (get-domain domain))
        kv (comp-key (node->concrete-object xml-node :object-node))]
    {:xml xml-node
     :xml-element-str (with-out-str (xml/emit-element (translate-xml-escape-char xml-node)))
     :xml-doc-str (with-out-str (xml/emit (translate-xml-escape-char xml-node)))
     :atom-attribute-test-statments (map (fn [[k v]]
                                           ((partial make-atom-attribute-test-statement comp-key) k v))
                                         kv)
     :test-value-map (into {} (map (fn [[k v]]
                                     [k (read-string v)])
                                   kv))}))

(def *component-factory-test-case-table*
  {:base (deffactory-test :base {:id 1001 :name "TestObject"})
   :combat-property (deffactory-test :combat-property)
   :monster-property (deffactory-test :monster-property)
   :rpg-property (deffactory-test :rpg-property)
   :vip-item (deffactory-test :vip-item)
   :trade (deffactory-test :trade)
   :seeding (deffactory-test :seeding)
   :item-base (deffactory-test :item-base)
   :role-info (deffactory-test :role-info)
   :currency (deffactory-test :currency)
   :scene-object (deffactory-test :scene-object {:scene-object-position 11})
   :skill-caster (deffactory-test :skill-caster)
   :weapon-equip (deffactory-test :weapon-equip)
   :skill-group-data (deffactory-test :skill-group-data)})

;(deffactory-test :combat-property {:attack 80 :defence 99 :speed 7 :mental 29 :dodge-rate 73 :crit-rate 1500 :magic-resistance 0x2C})
