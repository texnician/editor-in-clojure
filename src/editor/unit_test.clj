(ns editor.unit-test
  (:use (editor name-util core types component sid template domain))
  (:use [clojure.xml :as xml])
  (:require [clojure.string :as string]))

(with-test-domain (get-sub-domain :meta-go-component :global-enum)
  (deftemplate test-obj (base) (monster-property))
  (deftest-obj :id 1 :name "none")
  (xml/emit-element (:1 (get-domain :test-obj))))

(merge {:base {:id 1 :name "some"}} {:cool {:id 2 :name "ya"}}) 

(with-out-str (xml/emit (make-template-node :test-base-comp "测试BaseComponent" '(:base {:name "测试Component" :id 775}))))
(deffactory-test :base {:name "TestComponentName" :id 775})

(defn- make-attribute-test-statement [comp-key attr-key val]
  (let [attr-info (make-cpp-attribute comp-key attr-key)]
    ((:assert-eq attr-info) val)))

(defn component-factory-test-case [comp-key attr-map]
  (with-test-domain (get-sub-domain :meta-go-component :global-enum)
    (deftemplate comp-key ))
  (let [xml-node (make-template-node (keyword (format "test-%s-component" (name comp-key)))
                                     (format "Test case for %s" (cpp-component-factory-name comp-key))
                                     (list comp-key attr-map))
        kv (comp-key (node->concrete-object xml-node))]
    {:xml xml-node
     :xml-str (with-out-str (xml/emit-element xml-node))
     :attribute-test-statments (map (fn [[k v]]
                                      ((partial make-attribute-test-statement comp-key) k v))
                                    kv)}))
(deftemplate test-obj
  (:base))
(deftest-obj
  :name "TestComponentName" :id 775)

(component-factory-test-case :base {:name "TestComponentName" :id 775})
{:xml 
 :attribute-test-statments }
(node->concrete-object (make-template-node :test-base-component "Test case for BaseComponentFactory" '(:base {:name "测试Component" :id 775})))
(print (get-domain ))
