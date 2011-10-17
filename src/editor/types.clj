(ns editor.types
  (:use (editor core component name-util enum)))

(defn- cpp-define-type [comp-key attr-key atom-type]
  (if (atom-attribute? comp-key attr-key)
    atom-type
    (format "std::vector<%s>" atom-type)))

(defmulti define-type (fn [comp-key attr-key lang]
                        [(get-attribute-type comp-key attr-key) lang]))

(defmethod define-type [:int :cpp] [comp-key attr-key lang]
  (cpp-define-type comp-key attr-key "int"))

(defmethod define-type [:string :cpp] [comp-key attr-key lang]
  (cpp-define-type comp-key attr-key "std::string"))

(defmethod define-type [:enum :cpp] [comp-key attr-key lang]
  (cpp-define-type comp-key attr-key "int"))

(defmethod define-type [:bool :cpp] [comp-key attr-key lang]
  (cpp-define-type comp-key attr-key "bool"))

(defn- cpp-getter-return-type )
(defmulti getter-return-type (fn [comp-key attr-key lang]
                               [(get-attribute-type comp-key attr-key) lang]))

(defmethod getter-return-type [:int :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "int"
    "std::vector<int>"))

(defmethod getter-return-type [:string :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "const std::string&"
    "std::vector<std::string>"))

(defmethod getter-return-type [:enum :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "int"
    "std::vector<int>"))

(defmethod getter-return-type [:bool :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "bool"
    "std::vector<bool>"))

(defmulti attribute-member-name (fn [attr lang] lang))
(defmethod attribute-member-name :cpp [attr lang] (str (clojure-token->cpp-variable-token (name attr)) \_))

(defmulti variable-name (fn [attr lang] lang))
(defmethod variable-name :cpp [attr lang] (clojure-token->cpp-variable-token (name attr)))

(defmulti setter-argument-name (fn [attr lang] lang))
(defmethod setter-argument-name :cpp [attr lang] (clojure-token->cpp-variable-token (name attr)))

(defmulti setter-argument-type (fn [comp-key attr-key lang]
                                 [(get-attribute-type comp-key attr-key) lang]))

(defmethod setter-argument-type [:int :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "int"
    "const std::vector<int>&"))

(defmethod setter-argument-type [:string :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "const std::string&"
    "const std::vector<std::string>&"))

(defmethod setter-argument-type [:enum :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "int"
    "const std::vector<int>&"))

(defmethod setter-argument-type [:bool :cpp] [comp-key attr-key lang]
  (if (atom-attribute? comp-key attr-key)
    "bool"
    "const std::vector<bool>&"))

(defmulti attribute-default-value (fn [comp-key attr-key lang]
                                    [(get-attribute-type comp-key attr-key) lang]))

(defmethod attribute-default-value [:int :cpp] [comp-key attr-key lang]
  (get-attribute-default-value comp-key attr-key))

(defmethod attribute-default-value [:string :cpp] [comp-key attr-key lang]
  (str \" (get-attribute-default-value comp-key attr-key) \"))

(defmethod attribute-default-value [:bool :cpp] [comp-key attr-key lang]
  (get-attribute-default-value comp-key attr-key))

(defmethod attribute-default-value [:enum :cpp] [comp-key attr-key lang]
  (let [int-value (enum-int-value (keyword (:in-domain (get-attribute-meta-info comp-key attr-key #{:in-domain})))
                                  (keyword (get-attribute-default-value comp-key attr-key)))]
    (format "%d /* %s */"
            int-value
            (clojure-token->cpp-enum-token (get-attribute-default-value comp-key attr-key)))))

(defmulti attribute-default-array-value (fn [comp-key attr-key lang]
                                          [(get-attribute-type comp-key attr-key) lang]))

(defmethod attribute-default-array-value [:int :cpp] [comp-key attr-key lang]
  (if-not (atom-attribute? comp-key attr-key)
    (read-string (get-attribute-default-value comp-key attr-key))))

(defmethod attribute-default-array-value [:enum :cpp] [comp-key attr-key lang]
  (if-not (atom-attribute? comp-key attr-key)
    (let [domain-key (keyword (:in-domain (get-attribute-meta-info comp-key attr-key #{:in-domain})))]
      (vec (map (fn [x]
                  (let [int-value (enum-int-value domain-key (keyword x))]
                    (format "%d /* %s */" int-value (clojure-token->cpp-enum-token x))))
                (read-string (get-attribute-default-value comp-key attr-key)))))))

(defmethod attribute-default-array-value [:string :cpp] [comp-key attr-key lang]
  (if-not (atom-attribute? comp-key attr-key)
    (into [] (map #(str \" % \") (read-string (get-attribute-default-value comp-key attr-key))))))

(defmethod attribute-default-array-value [:bool :cpp] [comp-key attr-key lang]
  (if-not (atom-attribute? comp-key attr-key)
    (read-string (get-attribute-default-value comp-key attr-key))))

(defmulti attribute-test-statement (fn [comp-key attr-key lang & rest]
                                     [(get-attribute-type comp-key attr-key) lang]))

(defmethod attribute-test-statement [:int :cpp] [comp-key attr-key lang test-f value]
  (if (atom-attribute? comp-key attr-key)
    (format "%s(p->%s(), %s);" test-f (cpp-getter-name attr-key) value)))

(defmethod attribute-test-statement [:enum :cpp] [comp-key attr-key lang test-f value]
  (if (atom-attribute? comp-key attr-key)
    (let [int-value (enum-int-value (keyword (:in-domain (get-attribute-meta-info comp-key attr-key #{:in-domain})))
                                    (keyword value))]
      (format "%s(p->%s(), %s);" test-f (cpp-getter-name attr-key) int-value))))

(defmethod attribute-test-statement [:string :cpp] [comp-key attr-key lang test-f value]
  (if (atom-attribute? comp-key attr-key)
    (format "%s(p->%s(), std::string(\"%s\"));" test-f (cpp-getter-name attr-key) value)))

(defmethod attribute-test-statement [:bool :cpp] [comp-key attr-key lang test-f value]
  (if (atom-attribute? comp-key attr-key)
    (format "%s(p->%s(), %s);" test-f (cpp-getter-name attr-key) value)))

(defmulti array-attribute-item-test-statement (fn [comp-key attr-key lang & rest]
                                                [(get-attribute-type comp-key attr-key) lang]))

(defmethod array-attribute-item-test-statement [:int :cpp] [comp-key attr-key lang]
  (fn [vec idx value]
    (format "ASSERT_EQ(%s[%d], %d);" vec idx value)))

(defmethod array-attribute-item-test-statement [:enum :cpp] [comp-key attr-key lang]
  (fn [vec idx value]
    (let [int-value (enum-int-value (keyword (:in-domain (get-attribute-meta-info comp-key attr-key #{:in-domain})))
                                    (keyword value))]
      (format "ASSERT_EQ(%s[%d], %d);" vec idx int-value))))

(defmethod array-attribute-item-test-statement [:string :cpp] [comp-key attr-key lang]
  (fn [vec idx value]
    (format "ASSERT_EQ(%s[%d], std::string(\"%s\"));" vec idx value)))

(defmethod array-attribute-item-test-statement [:bool :cpp] [comp-key attr-key lang]
  (fn [vec idx value]
    (format "ASSERT_EQ(%s[%d], %s);" vec idx value)))

(defn attribute-assert-eq [comp-key attr-key lang]
  (fn [value]
    (attribute-test-statement comp-key attr-key lang "ASSERT_EQ" value)))

(defn attribute-assert-ne [comp-key attr-key lang]
  (fn [value]
    (attribute-test-statement comp-key attr-key lang "ASSERT_NE" value)))

(defn make-cpp-attribute [comp-key attr-key]
  {:key attr-key
   :raw-type (get-attribute-type comp-key attr-key)
   :raw-name (name attr-key)
   :variable-name (variable-name attr-key :cpp)
   :member-name (attribute-member-name attr-key :cpp)
   :define-type (define-type comp-key attr-key :cpp)
   :getter-return-type (getter-return-type comp-key attr-key :cpp)
   :getter-name (cpp-getter-name attr-key)
   :setter-name (cpp-setter-name attr-key)
   :setter-argument-type (setter-argument-type comp-key attr-key :cpp)
   :setter-argument-name (setter-argument-name attr-key :cpp)
   :default-value (attribute-default-value comp-key attr-key :cpp)
   :default-array-value (attribute-default-array-value comp-key attr-key :cpp)
   :doc (:doc (get-attribute-meta-info comp-key attr-key #{:doc}))
   :array-item-test (array-attribute-item-test-statement comp-key attr-key :cpp)
   :assert-eq (attribute-assert-eq comp-key attr-key :cpp)
   :assert-ne (attribute-assert-ne comp-key attr-key :cpp)})
