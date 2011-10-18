(ns editor.st
  (:import (org.stringtemplate.v4 ST
                                  STGroup
                                  STGroupDir
                                  STGroupFile
                                  STGroupString
                                  AttributeRenderer))
  (:import (java.io File))
  (:import (java.util Formatter
                      Locale))
  (:use (editor name-util core types component sid template unit-test))
  (:use [clojure.xml :as xml])
  (:require [clojure.string :as string]))

(def *cpp-component-stg* "st/cpp/component.stg")
(def *cpp-component-factory-stg* "st/cpp/component_factory.stg")
(def *cpp-test-factory-stg* "st/cpp/test_factory.stg")
(def *cpp-game-object-factory-stg* "st/cpp/game_object_factory.stg")

(def *manifest* "Automatically generated by happy world editor, DO NOT edit by hands.")

(defn- st-add-op [attr-value]
  (if-let [[attr value] attr-value]
    (fn [st]
      (.add st attr value))))

(defn- st-render-op [l]
  (fn [st]
    (.render st l)))

(defn- component-class-file [st component-class]
  (fn [comp-key]
    (doseq [op (map st-add-op [["filename", (cpp-component-gen-header-filename comp-key)]
                               ["date" (get-date)]
                               ["manifest" *manifest*]
                               ["name" (cpp-component-gen-header-define comp-key)]
                               ["body" (component-class comp-key)]])]
      (op st))
    (.render st)))

(defn- component-class [st component-bases initialize-list initialize-members attributes getters-and-setters]
  (fn [comp-key]
    (doseq [op (map st-add-op (list ["name" (cpp-component-gen-name comp-key)]
                                    ["comp_name" (cpp-component-name comp-key)]
                                    ["bases" (component-bases comp-key)]
                                    ["initialize_list" (initialize-list comp-key)]
                                    ["initialize_members" (initialize-members comp-key)]
                                    ["sid" (format "%#x" (gen-sid (cpp-component-name comp-key)))]
                                    ["attributes" (attributes comp-key)]
                                    ["getters_and_setters" (getters-and-setters comp-key)]))]
      (op st))
    (.render st)))

(defn- component-bases [st]
  (fn [comp-key]
    (doseq [op (map st-add-op '(["base_list" "IComponent"]))]
      (op st))
    (.render st)))

(defn- initialize-list [st attr-builder]
  (fn [comp-key]
    (doseq [op (map st-add-op (mapcat (comp  #(list ["attrs" (:member-name %)] ["values" (:default-value %)])
                                             (partial attr-builder comp-key))
                                      (filter (fn [x]
                                                (atom-attribute? comp-key x)) (component-attribute-keys comp-key))))]
      (op st))
    (.render st 80)))

(defn- initialize-member-block [group attr-builder]
  (fn [comp-key attr-key]
    (assert (not (atom-attribute? comp-key attr-key)))
    (let [attr (attr-builder comp-key attr-key)
          array-values (:default-array-value attr)]
      (let [st (.getInstanceOf group "initialize_vector_block")]
        (doseq [op (map st-add-op (map (fn [x]
                                         ["push_backs" (format "this->%s.push_back(%s);"
                                                               (:member-name attr)
                                                               x)])
                                       array-values))]
          (op st))
        (.render st)))))

(defn- initialize-members [st group attr-builder]
  (fn [comp-key]
    (let [attr-list (filter (fn [x]
                              (not (atom-attribute? comp-key x)))
                            (component-attribute-keys comp-key))]
      (doseq [op (map st-add-op (map (fn [x]
                                       ["blocks" ((initialize-member-block group attr-builder) comp-key x)]) attr-list))]
        (op st))
      (.render st))))

(defn- doc-lines [st]
  (fn [attr]
    (let [lines (mapcat #(string/split % #";") (map #(string/replace % #"^\s+" "") (string/split (:doc attr) #"\n")))]
      (doseq [op (map st-add-op (map (fn [x]
                                       ["lines" x])
                                      lines))]
        (op st))
      (.render st))))

(defn- attributes [st group attr-builder]
  (fn [comp-key]
    (doseq [op (map st-add-op (mapcat (comp #(list ["types" (:define-type %)]
                                                   ["names" (:member-name %)]
                                                   ["docs" ((doc-lines (.getInstanceOf group "doc_lines")) %)])
                                             (partial attr-builder comp-key))
                                       (component-attribute-keys comp-key)))]
      (op st))
    (.render st)))

(defn- getters-and-setters [st attr-builder]
  (fn [comp-key]
    (doseq [op (map st-add-op (mapcat (comp #(list ["types" (:getter-return-type %)]
                                                   ["getters" (:getter-name %)]
                                                   ["setters" (:setter-name %)]
                                                   ["setter_arg_types" (:setter-argument-type %)]
                                                   ["m_names" (:member-name %)]
                                                   ["arg_names" (:setter-argument-name %)])
                                             (partial attr-builder comp-key))
                                       (component-attribute-keys comp-key)))]
      (op st))
    (.render st)))

(defn gen-component [comp-key]
  "生成一个Cpp Component类头文件"
  (let [group (STGroupFile. *cpp-component-stg*)
        bases-st (.getInstanceOf group "bases")
        bases (component-bases bases-st)
        attributes-st (.getInstanceOf group "attributes")
        attrs (attributes attributes-st group make-cpp-attribute)
        initialize-list-st (.getInstanceOf group "initialize_list")
        initialize-list-fn (initialize-list initialize-list-st make-cpp-attribute)
        initialize-members-st (.getInstanceOf group "initialize_members")
        initialize-members-fn (initialize-members initialize-members-st group make-cpp-attribute)
        getters-and-setters-st (.getInstanceOf group "getters_and_setters")
        gas (getters-and-setters getters-and-setters-st make-cpp-attribute)
        class-st (.getInstanceOf group "component_class")
        cls (component-class class-st bases initialize-list-fn initialize-members-fn attrs gas)
        file-st (.getInstanceOf group "component_header")]
    (spit (cpp-component-gen-header-filename comp-key) ((component-class-file file-st cls) comp-key))))


(defn- component-sid-initialize [st]
  (fn [comp-key-list]
    (let [name-lens (map #(-> % cpp-component-gen-name count) comp-key-list)
          max-len (apply max name-lens)
          padding-map (zipmap comp-key-list (map (comp #(apply str %) #(repeat % \space) (partial - max-len)) name-lens))]
      (doseq [op (map st-add-op (list* ["filename" (cpp-component-sid-initialize-filename)]
                                       ["manifest" *manifest*]
                                       ["date" (get-date)]
                                       (mapcat (fn [x]
                                                 (list ["comp_gen_headers" (cpp-component-gen-header-filename x)]
                                                       ["comps" (cpp-component-gen-name x)]
                                                       ["paddings" (padding-map x)]
                                                       ["sids" (format "%#x" (gen-sid (cpp-component-name x)))])) comp-key-list)))]
        (op st)))
    (.render st)))

(defn gen-component-sid-initialize [comp-key-list]
  "生成初始化Component sid的cpp文件"
  (let [group (STGroupFile. *cpp-component-stg*)
        component-sid-initialize-fn (component-sid-initialize (.getInstanceOf group "component_sid_initialize"))]
    (spit (cpp-component-sid-initialize-filename) (component-sid-initialize-fn comp-key-list))))

;(gen-component :monster-property)

(defn- attribute-set-block-selector [group]
  (fn [attr]
    (let [raw-type (:raw-type attr)
          [st op-list] (cond (= :int raw-type) [(.getInstanceOf group "set_int_value")
                                                (list ["attr" (:variable-name attr)]
                                                      ["setter" (:setter-name attr)])]  
                             (= :string raw-type) [(.getInstanceOf group "set_string_value")
                                                   (list ["attr" (:variable-name attr)]
                                                         ["setter" (:setter-name attr)])] 
                             (= :enum raw-type) [(.getInstanceOf group "set_enum_value")
                                                 (list ["attr" (:variable-name attr)]
                                                       ["setter" (:setter-name attr)]
                                                       ["enum_int_val" (name *enum-int-value-tag*)])]
                             (= :bool raw-type) [(.getInstanceOf group "set_bool_value")
                                                 (list ["attr" (:variable-name attr)]
                                                       ["setter" (:setter-name attr)])]
                             :else [(.getInstanceOf group "set_unknown_value")
                                    (list ["attr" (:variable-name attr)]
                                          ["setter" (:setter-name attr)]
                                          ["type" (name raw-type)])])]
      (doseq [op (map st-add-op op-list)]
        (op st))
      (.render st))))

(defn- st-has-attribute? [st attr]
  (let [attrs (keys (.getAttributes st))]
    (some #{attr} attrs)))

(defn- build-atom-attribute [st attr-builder set-block-selector]
  (fn [comp-key attr-key]
    (let [op-table ((comp #(list ["component_name" (cpp-component-name comp-key)]
                                 ["variable_name" (:variable-name %)]
                                 ["attr_name" (:raw-name %)]
                                 ["set_block" ((set-block-selector) %)])
                          (partial attr-builder comp-key)) attr-key)]
      (doseq [op (map st-add-op op-table)]
        (op st))
      (.render st))))

(defn- array-attribute-set-block-selector [group]
  (fn [attr]
    (let [raw-type (:raw-type attr)
          [st op-list] (cond (= :int raw-type) [(.getInstanceOf group "set_int_array_value")
                                                (list ["setter" (:setter-name attr)]
                                                      ["attr" (:variable-name attr)])]
                             (= :string raw-type) [(.getInstanceOf group "set_string_array_value")
                                                   (list ["setter" (:setter-name attr)]
                                                         ["attr" (:variable-name attr)])]
                             (= :enum raw-type) [(.getInstanceOf group "set_enum_array_value")
                                                 (list ["attr" (:variable-name attr)]
                                                       ["setter" (:setter-name attr)]
                                                       ["enum_int_val" (name *enum-int-value-tag*)])]
                             (= :bool raw-type) [(.getInstanceOf group "set_bool_array_value")
                                                 (list ["attr" (:variable-name attr)]
                                                       ["setter" (:setter-name attr)])]
                             :else [(.getInstanceOf group "set_unknown_value")
                                    (list ["attr" (:variable-name attr)]
                                          ["setter" (:setter-name attr)]
                                          ["type" (name raw-type)])])]
      (doseq [op (map st-add-op op-list)]
        (op st))
      (.render st))))

(defn- build-array-attribute [st attr-builder set-block-selector]
  (fn [comp-key attr-key]
    (let [op-table ((comp #(list ["component_name" (cpp-component-name comp-key)]
                                 ["variable_name" (:variable-name %)]
                                 ["attr_name" (:raw-name %)]
                                 ["set_block" ((set-block-selector) %)])
                                 (partial attr-builder comp-key)) attr-key)]
      (doseq [op (map st-add-op op-table)]
        (op st))
      (.render st))))

(defn- factory-build-attributes [group attr-builder]
  (fn [comp-key]
    (let [attr-list (component-attribute-keys comp-key)]
      (map (fn [x]
             (let [[st builder selector] (if (atom-attribute? comp-key x)
                                           (vector (.getInstanceOf group "build_atom_attribute")
                                                   build-atom-attribute
                                                   (partial attribute-set-block-selector group))
                                           (vector (.getInstanceOf group "build_array_attribute")
                                                   build-array-attribute
                                                   (partial array-attribute-set-block-selector group)))]
               ((builder st attr-builder selector) comp-key x)))
           attr-list))))

(defn- factory-find-component-node [st]
  (fn [comp-key]
    (doseq [op (map st-add-op (list ["go_tag" (name *go-component-tag*)]
                                    ["raw_name" (name comp-key)]
                                    ["class_name" (cpp-component-name comp-key)]))]
      (op st))
    (.render st)))

(defn- component-factory-define [st find-component-node build-attributes]
  (fn [comp-key]
    (doseq [op (map st-add-op (list* ["class_name" (cpp-component-name comp-key)]
                                     ["factory_name" (cpp-component-factory-name comp-key)]
                                     ["find_component_node" (find-component-node comp-key)]
                                     (map #(vector "build_attributes" %)
                                          (build-attributes comp-key))))]
      (op st))
    (.render st)))

(def *component-factory-header-name* "component_factory.h")

(def *component-factory-cpp-name* "component_factory.cpp")

(def *component-factory-test-filename* "component_factory_test.cpp")

(defn- component-factory-cpp [st group]
  (fn [comp-key-list]
    (doseq [op (map st-add-op (list* ["file_name" *component-factory-cpp-name*]
                                     ["date", (get-date)]
                                     (mapcat (fn [x]
                                               (let [build-attributes (factory-build-attributes
                                                                       group
                                                                       make-cpp-attribute)
                                                     find-component-node (factory-find-component-node
                                                                          (.getInstanceOf group "find_component_node"))
                                                     define-st (component-factory-define
                                                                (.getInstanceOf group "component_factory_define") find-component-node
                                                                build-attributes)]
                                                 (list ["component_headers", (cpp-component-header-filename x)]
                                                       ["component_factory_defines", (define-st x)])))
                                             comp-key-list)))]
      (op st))
    (.render st)))

(defn- component-factory-decl [st]
  (fn [comp-key]
    (doseq [op (map st-add-op (list ["factory_name" (cpp-component-factory-name comp-key)]
                                    ["interface_name" "IComponent"]))]
      (op st))
    (.render st)))

(defn- component-factory-header [st group]
  (fn [comp-key-list]
    (doseq [op (map st-add-op (list* ["file_name" *component-factory-header-name*]
                                     ["guard" "_COMPONENT_FACTORY_H_"]
                                     ["manifest" *manifest*]
                                     ["date" (get-date)]
                                     (map (fn [x]
                                            (let [decl-st (component-factory-decl
                                                           (.getInstanceOf group "component_factory_decl"))]
                                              ["factory_decls" (decl-st x)]))
                                          comp-key-list)))]
      (op st))
    (.render st)))

(defn gen-component-factory [comp-key-list]
  "生成一个Cpp Component类Factory"
  (let [group (STGroupFile. *cpp-component-factory-stg*)
        cpp-st (component-factory-cpp (.getInstanceOf group "component_factory_cpp") group)
        header-st (component-factory-header (.getInstanceOf group "component_factory_header") group)]
    (spit *component-factory-header-name* (header-st comp-key-list))
    (spit *component-factory-cpp-name* (cpp-st comp-key-list))))

; (gen-component-factory :combat-property)
'(gen-component-factory '(:combat-property :monster-property :rpg-property :vip-item :trade :seeding :item-base :base))
'(doseq [c '(:combat-property :monster-property :rpg-property :vip-item :trade :seeding :item-base :base)]
  (gen-component c))

(defn- array-attr-test-block [st test-values]
  (fn [comp-key attr-key]
    (let [attr-info (make-cpp-attribute comp-key attr-key)
          test-fn (:array-item-test attr-info)
          idx-seq (range (count test-values))]
      (doseq [op (map st-add-op (list* ["array_type" (:define-type attr-info)]
                                       ["getter" (:getter-name attr-info)]
                                       (map (fn [i v]
                                              (vector "statements" (test-fn "vec_val" i v)))
                                            idx-seq test-values)))]
        (op st))
      (.render st))))

(defn- attr-test-group [st group]
  (fn [comp-key]
    (let [attr-list (component-attribute-keys comp-key)
          test-case (comp-key *component-factory-test-case-table*)]
      (doseq [op (map st-add-op (map #(vector "statements" %) (:atom-attribute-test-statments test-case)))]
        (op st))
      (doseq [op (map st-add-op (map (fn [x]
                                       (let [st-fn (array-attr-test-block (.getInstanceOf group "array_attr_test_block")
                                                                          (-> test-case :test-value-map x))]
                                         (vector "statements" (st-fn comp-key x))))
                                     (filter #(not (atom-attribute? comp-key %)) attr-list)))]
        (op st)))
    (.render st)))

(defn- comp-test [st group]
  (fn [comp-key]
    (let [attr-test-group-fn (attr-test-group (.getInstanceOf group "attr_test_group") group)
          test-case (comp-key *component-factory-test-case-table*)]
      (doseq [op (map st-add-op (list* ["comp_name" (cpp-component-name comp-key)]
                                       ["factory_name" (cpp-component-factory-name comp-key)]
                                       ["attr_test_group" (attr-test-group-fn comp-key)]
                                       (map #(vector "xml_string" %) (string/split-lines (:xml-element-str test-case)))
                                       ))]
        (op st))
      (.render st))))

(defn- component-factory-test-file [st group]
  (fn [comp-key-list]
    (doseq [op (map st-add-op (list* ["manifest" *manifest*]
                                     ["filename" (cpp-component-factory-test-filename)]
                                     ["date" (get-date)]
                                     (concat (map #(vector "comp_headers" (cpp-component-header-filename %))
                                                  comp-key-list)
                                             (map #(vector "comp_tests" ((comp-test (.getInstanceOf group "comp_test") group) %))
                                                  comp-key-list))))]
      (op st))
    (.render st)))

(defn gen-component-factory-test [comp-key-list]
  "生成cpp Component Factory的单元测试代码"
  (let [group (STGroupFile. *cpp-test-factory-stg*)
        test-factory-file-fn (component-factory-test-file (.getInstanceOf group "test_factory_file")
                                                          group)]
    (spit *component-factory-test-filename* (test-factory-file-fn comp-key-list))))

'(gen-component-factory-test '(:combat-property :monster-property :rpg-property :vip-item :trade :seeding :item-base :base))

(def *game-object-factory-header-name* "game_object_factory.h")

(def *game-object-factory-cpp-name* "game_object_factory.cpp")

(def *game-object-factory-test-filename* "game_object_factory_test.cpp")

(defn- game-object-decl [st]
  (fn [go-key]
    (doseq [op (map st-add-op (list ["factory_name" (cpp-game-object-factory-name go-key)]
                                    ["game_object_interface" "IGameObject"]))]
      (op st))
    (.render st)))

(defn- game-object-factory-header [st group]
  (fn [go-list]
    (doseq [op (map st-add-op (list* ["file_name" *game-object-factory-header-name*]
                                     ["guard" "_GAME_OBJECT_FACTORY_H_"]
                                     ["manifest" *manifest*]
                                     ["date" (get-date)]
                                     (map (fn [x]
                                            (let [decl-fn (game-object-decl
                                                           (.getInstanceOf group "game_object_factory_decl"))]
                                              (vector "factory_decls" (decl-fn x))))
                                          go-list)))]
      (op st))
    (.render st)))

(defn- game-object-create-component [st factory-name]
  (fn [comp-key]
    (doseq [op (map st-add-op (list ["factory_name" factory-name]
                                    ["comp_interface" "IComponent"]
                                    ["comp_name" (cpp-component-name comp-key)]
                                    ["comp_factory_name" (cpp-component-factory-name comp-key)]))]
      (op st))
    (.render st)))

(defn- game-object-factory-define [st group]
  (fn [obj-key]
    (let [comp-list (keys (make-concrete-template obj-key))]
      (doseq [op (map st-add-op (list* ["factory_name" (cpp-game-object-factory-name obj-key)]
                                       ["obj_interface" "IGameObject"]
                                       (map (fn [x]
                                              (vector "create_components"
                                                      ((game-object-create-component (.getInstanceOf group "create_component")
                                                                                     (cpp-game-object-factory-name obj-key)) x)))
                                            comp-list)))]
        (op st)))
    (.render st)))

(defn- game-object-factory-cpp [st group]
  (fn [go-list]
    (doseq [op (map st-add-op (list* ["file_name" *game-object-factory-cpp-name*]
                                     ["manifest" *manifest*]
                                     ["date" (get-date)]
                                     ["header" *game-object-factory-header-name*]
                                     (map (fn [x]
                                            (vector "factory_defines"
                                                    ((game-object-factory-define (.getInstanceOf group "game_object_factory_define") group) x)))
                                          go-list)))]
      (op st))
    (.render st)))

(defn gen-game-object-factory [go-list]
  "生成一个Cpp GameObject类Factory"
  (let [group (STGroupFile. *cpp-game-object-factory-stg*)
        cpp-st (game-object-factory-cpp (.getInstanceOf group "game_object_factory_cpp") group)
        header-st (game-object-factory-header (.getInstanceOf group "game_object_factory_header") group)]
    (spit *game-object-factory-cpp-name* (cpp-st go-list))
    (spit *game-object-factory-header-name* (header-st go-list))))

'(gen-game-object-factory '(:monster :seed :fruit))
