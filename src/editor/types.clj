(ns editor.types
  (:use (editor core component name-util enum)))

(defn get-attribute-type [comp-key attr-key]
  "返回一个attribute的类型"
  (let [type-info (get-attribute-meta-info comp-key attr-key #{:type})]
    (keyword (:type type-info))))

(defn get-attribute-default-value [comp-key attr-key]
  "返回一个attribute的default value"
  (let [type-info (get-attribute-meta-info comp-key attr-key #{:default})]
    (str (:default type-info))))

(defmulti define-type (fn [comp-key attr-key lang]
                        [(get-attribute-type comp-key attr-key) lang]))

(defmethod define-type [:int :cpp] [comp-key attr-key lang] "int")
(defmethod define-type [:string :cpp] [comp-key attr-key lang] "std::string")
(defmethod define-type [:enum :cpp] [comp-key attr-key lang] "int")
(defmethod define-type [:bool :cpp] [comp-key attr-key lang] "bool")

(defmulti getter-return-type (fn [comp-key attr-key lang]
                               [(get-attribute-type comp-key attr-key) lang]))

(defmethod getter-return-type [:int :cpp] [comp-key attr-key lang]
  "int")

(defmethod getter-return-type [:string :cpp] [comp-key attr-key lang]
  "const std::string&")

(defmethod getter-return-type [:enum :cpp] [comp-key attr-key lang]
  "int")

(defmethod getter-return-type [:bool :cpp] [comp-key attr-key lang]
  "bool")

(defmulti attribute-member-name (fn [attr lang] lang))
(defmethod attribute-member-name :cpp [attr lang] (str (clojure-token->cpp-variable-token (name attr)) \_))

(defmulti setter-argument-name (fn [attr lang] lang))
(defmethod setter-argument-name :cpp [attr lang] (clojure-token->cpp-variable-token (name attr)))

(defmulti setter-argument-type (fn [comp-key attr-key lang]
                                 [(get-attribute-type comp-key attr-key) lang]))

(defmethod setter-argument-type [:int :cpp] [comp-key attr-key lang]
  "int")

(defmethod setter-argument-type [:string :cpp] [comp-key attr-key lang]
  "const std::string&")

(defmethod setter-argument-type [:enum :cpp] [comp-key attr-key lang]
  "int")

(defmethod setter-argument-type [:bool :cpp] [comp-key attr-key lang]
  "bool")

(defmulti attribute-default-value (fn [comp-key attr-key lang]
                                    [(get-attribute-type comp-key attr-key) lang]))

(defmethod attribute-default-value [:int :cpp] [comp-key attr-key lang]
  (get-attribute-default-value comp-key attr-key))

(defmethod attribute-default-value [:string :cpp] [comp-key attr-key lang]
  (str \" (get-attribute-default-value comp-key attr-key) \"))

(defmethod attribute-default-value [:bool :cpp] [comp-key attr-key lang]
  (get-attribute-default-value comp-key attr-key))

(defmethod attribute-default-value [:enum :cpp] [comp-key attr-key lang]
  (let [int-value ((fn-global-enum-map (keyword (:in-domain (get-attribute-meta-info comp-key attr-key #{:in-domain}))))
                   (keyword (get-attribute-default-value comp-key attr-key)))]
    (format "%d /* %s */"
            int-value
            (clojure-token->cpp-enum-token (get-attribute-default-value comp-key attr-key)))))

(defn make-cpp-attribute [comp-key attr-key]
  {:raw-name (name attr-key)
   :member-name (attribute-member-name attr-key :cpp)
   :define-type (define-type comp-key attr-key :cpp)
   :getter-return-type (getter-return-type comp-key attr-key :cpp)
   :getter-name (cpp-getter-name attr-key)
   :setter-name (cpp-setter-name attr-key)
   :setter-argument-type (setter-argument-type comp-key attr-key :cpp)
   :setter-argument-name (setter-argument-name attr-key :cpp)
   :default-value (attribute-default-value comp-key attr-key :cpp)
   :doc (:doc (get-attribute-meta-info comp-key attr-key #{:doc}))})
