(ns editor.sql
  (:use (editor name-util core types component sid template domain error enum))
  (:require [clojure.string :as string]))

(def *mysql-keyword-talbe*
  '#{ACCESSIBLE ADD ALL ALTER ANALYZE AND AS ASC ASENSITIVE
     BEFORE BETWEEN BIGINT BINARY BLOB BOTH BY CALL CASCADE CASE CHANGE CHAR 
     CHARACTER CHECK COLLATE
     COLUMN CONDITION CONSTRAINT
     CONTINUE CONVERT CREATE
     CROSS CURRENT_DATE CURRENT_TIME
     CURRENT_TIMESTAMP CURRENT_USER CURSOR
     DATABASE DATABASES DAY_HOUR
     DAY_MICROSECOND DAY_MINUTE DAY_SECOND
     DEC DECIMAL DECLARE
     DEFAULT DELAYED DELETE
     DESC DESCRIBE DETERMINISTIC
     DISTINCT DISTINCTROW DIV
     DOUBLE DROP DUAL
     EACH ELSE ELSEIF
     ENCLOSED ESCAPED EXISTS
     EXIT EXPLAIN FALSE
     FETCH FLOAT FLOAT4
     FLOAT8 FOR FORCE
     FOREIGN FROM FULLTEXT
     GRANT GROUP HAVING
     HIGH_PRIORITY HOUR_MICROSECOND HOUR_MINUTE
     HOUR_SECOND IF IGNORE
     IN INDEX INFILE
     INNER INOUT INSENSITIVE
     INSERT INT INT1
     INT2 INT3 INT4
     INT8 INTEGER INTERVAL
     INTO IS ITERATE
     JOIN KEY KEYS
     KILL LEADING LEAVE
     LEFT LIKE LIMIT
     LINEAR LINES LOAD
     LOCALTIME LOCALTIMESTAMP LOCK
     LONG LONGBLOB LONGTEXT
     LOOP LOW_PRIORITY MASTER_SSL_VERIFY_SERVER_CERT
     MATCH MAXVALUE MEDIUMBLOB
     MEDIUMINT MEDIUMTEXT MIDDLEINT
     MINUTE_MICROSECOND MINUTE_SECOND MOD
     MODIFIES NATURAL NOT
     NO_WRITE_TO_BINLOG NULL NUMERIC
     ON OPTIMIZE OPTION
     OPTIONALLY OR ORDER
     OUT OUTER OUTFILE
     PRECISION PRIMARY PROCEDURE
     PURGE RANGE READ
     READS READ_WRITE REAL
     REFERENCES REGEXP RELEASE
     RENAME REPEAT REPLACE
     REQUIRE RESIGNAL RESTRICT
     RETURN REVOKE RIGHT
     RLIKE SCHEMA SCHEMAS
     SECOND_MICROSECOND SELECT SENSITIVE
     SEPARATOR SET SHOW
     SIGNAL SMALLINT SPATIAL
     SPECIFIC SQL SQLEXCEPTION
     SQLSTATE SQLWARNING SQL_BIG_RESULT
     SQL_CALC_FOUND_ROWS SQL_SMALL_RESULT SSL
     STARTING STRAIGHT_JOIN TABLE
     TERMINATED THEN TINYBLOB
     TINYINT TINYTEXT TO
     TRAILING TRIGGER TRUE
     UNDO UNION UNIQUE
     UNLOCK UNSIGNED UPDATE
     USAGE USE USING
     UTC_DATE UTC_TIME UTC_TIMESTAMP
     VALUES VARBINARY VARCHAR
     VARCHARACTER VARYING WHEN
     WHERE WHILE WITH
     WRITE XOR YEAR_MONTH
     ZEROFILL})

(declare create-sql)
(declare parse-arg-spec)

(defn- parse-arg-spec [arg-spec]
  (let [spec-seq (partition 2 arg-spec)]
    (zipmap (map #(-> % second keyword) spec-seq)
            (map first spec-seq))))

(defn- process-sql-seq [f sql-seq]
  (loop [acc () x sql-seq]
    (if (nil? x)
      acc
      (recur (cons (f (first x)) acc) (next x)))))

(defn- translate-token [token]
  (if (symbol? token)
    (symbol (cpp-variable-token->clojure-token token))
    token))

(defn- translate-newline [token]
  (if (string? token)
    (string/replace token "\n" " ")
    token))

(declare *mysql-keyword-talbe*)

(defn- translate-keyword [token]
  (if (string? token)
    (let [word-seq (re-seq #"\w+" token)]
      (loop [acc token, w word-seq]
        (if (empty? w)
          acc
          (let [h (first w)]
            (if (contains? *mysql-keyword-talbe* (-> h string/upper-case symbol))
              (recur (string/replace acc h (string/upper-case h)) (next w))
              (recur acc (next w))))
          )))
    token))

(defn- translate-separator [token]
  (if (= '| token)
    ", "
    token))

(defn- filter-args [sql-seq]
  (filter #(symbol? %) sql-seq))

(defn- split-object-token [token]
  (re-matches #"(.+)\.(.+)" token))

(defn- build-arg-table [arg-spec obj-list]
  "输入arg specific, 返回一个生成 argument table 
'{:player {:access-from :go :type :go :attrs [:name :status :ptid :digit-id]}
  :table {:type :string}}'
 的函数"
  (if (empty? obj-list)
    {}
    (let [acc (build-arg-table arg-spec (next obj-list))]
      (if-let [[_, obj, attr] (split-object-token (-> obj-list first name))]
        (let [obj-key (-> obj keyword)
              t (arg-spec obj-key)
              attr-key (-> attr keyword)]
          (if (contains? acc obj-key)
            (assoc acc obj-key (assoc (acc obj-key) :attrs
                                      (conj ((acc obj-key) :attrs) attr-key)))
            (assoc acc obj-key {:access-from t :type t :attrs [attr-key]})))
        (let [atom-key (-> obj-list first keyword)]
          (assoc acc atom-key {:type (arg-spec atom-key)}))))))

(defn- arg-type [arg-key arg-table]
  (assert (contains? arg-table arg-key))
  (-> arg-key arg-table :type))

(defn- attr-comp [attr-key]
  (find-attribute-component attr-key (keys (get-domain :meta-go-component))))

(defn- attr-type [attr-key]
  (if-let [comp-key (attr-comp attr-key)]
    (:raw-type (make-cpp-attribute comp-key attr-key))
    (emit-error :cant-find-attribute-in-sql-command {:attr (name attr-key)})))

(defmulti sql-placeholder->fmt-string (fn [lang]
                                        lang))

(defn- arg-fmt-type [arg-table sym]
  (if-let [[_, obj, attr] (split-object-token (-> sym name))]
                                (let [obj-key (keyword obj)
                                      attr-key (keyword attr)]
                                  (attr-type attr-key))
                                (let [atom-key (-> sym keyword)]
                                  (arg-type atom-key arg-table))))

(defmethod sql-placeholder->fmt-string :c [lang]
  (fn [arg-table sym]
    (*c-sql-string-fmt-table* (arg-fmt-type arg-table sym))))

(defmethod sql-placeholder->fmt-string :clojure [lang]
  (fn [arg-table sym]
    (*java-sql-string-fmt-table* (arg-fmt-type arg-table sym))))

(defn- translate-fmt-string [lang arg-table]
  (fn [token]
    (if (symbol? token)
      ((sql-placeholder->fmt-string lang) arg-table token)
      token)))

(defn- make-full-sql-fmt-string [lang arg-table sql-template]
  (string/replace (string/replace (string/join \space (map (translate-fmt-string lang arg-table) sql-template))
                                  #"\s\s+" " ") #"\s," ","))

(defmulti target-arg-list-maker (fn [lang arg-table]
                                  lang))

(defn- make-go-json-value-statement [obj-key attr-key]
  (if-let [comp-key (attr-comp attr-key)]
    (let [attr-info (make-cpp-attribute comp-key attr-key)]
      (format "%s[\"%s\"][\"%s\"].%s" (-> obj-key name clojure-token->cpp-variable-token)
              (name comp-key) (name attr-key) (*json-printf-sql-converter-table* (:raw-type attr-info))))
    (emit-error :cant-find-attribute-in-sql-command {:attr (name attr-key)})))

(defn- make-co-json-value-statement [obj-key attr-key]
  (if-let [comp-key (attr-comp attr-key)]
    (let [attr-info (make-cpp-attribute comp-key attr-key)]
      (format "%s[\"%s\"].%s" (-> obj-key name clojure-token->cpp-variable-token)
              (name attr-key) (*json-printf-sql-converter-table* (:raw-type attr-info))))
    (emit-error :cant-find-attribute-in-sql-command {:attr (name attr-key)})))

(defn- make-atom-c-arg-statement [sym]
  (-> sym name clojure-token->cpp-variable-token))

(defmethod target-arg-list-maker :c [lang arg-table]
  (fn [sym]
    (if-let [[_, obj, attr] (split-object-token (name sym))]
      (let [obj-key (keyword obj)
            attr-key (keyword attr)
            t (arg-type obj-key arg-table)]
        (if (= t :go)
          (make-go-json-value-statement obj-key attr-key)
          (make-co-json-value-statement obj-key attr-key)))
      (make-atom-c-arg-statement sym))))

(defmethod target-arg-list-maker :clojure [lang arg-type]
  (fn [sym]
    (if-let [[_, obj, attr] (split-object-token (name sym))]
      (let [attr-key (keyword attr)]
        (list (symbol obj) attr-key))
      sym)))

(defn- make-target-arg-list [lang arg-table sql-template]
  (let [arg-sym-list (filter-args sql-template)
        maker (target-arg-list-maker lang arg-table)]
    (map maker arg-sym-list)))

(defmacro defsql [func-name raw-arg-spec & body]
  (let [doc (if (-> body first string?) (first body) "This function was created by a lazy man(woman).")
        sql-seq (if (-> body first string?) (second body) (apply list (first body)))
        sql-template (map (comp translate-token translate-keyword translate-newline translate-separator) sql-seq)
        arg-spec (vec (map translate-token raw-arg-spec))
        arg-table (build-arg-table (parse-arg-spec arg-spec) (filter-args sql-template))]
    `(do (defn ~func-name ~(vec (filter symbol? arg-spec))
           (format ~(make-full-sql-fmt-string :clojure arg-table sql-template) 
                   ~@(make-target-arg-list :clojure arg-table sql-template)))
         (register-in-domain :sql ~(keyword func-name)
                             {:fmt-str ~(make-full-sql-fmt-string :c arg-table sql-template)
                              :arg-list '~(make-target-arg-list :c arg-table sql-template)
                              :arg-spec '~arg-spec
                              :func-name ~(name func-name)
                              :doc ~doc}))))

(defn sql-func-info [func-key]
  (func-key (get-domain :sql)))

(defn sql-func-arg-decl [[t sym]]
  (format "%s %s" (*c-sql-func-arg-decl-table* t) (-> sym name (clojure-token->cpp-variable-token))))

(defn- get-attribute-sql-value [comp-key attr-key]
  (let [attr-type (get-attribute-type comp-key attr-key)
        value-str (get-attribute-default-value comp-key attr-key)]
    (cond (= attr-type :string) value-str
          (= attr-type :enum) (enum-int-value (get-attribute-in-domain comp-key attr-key) (keyword value-str))
          (= attr-type :bool) (if (read-string value-str) 1 0)
          :else (read-string value-str))))

(defn go [go-key & attr-map]
  (let [attr-list (get-template-attribute-list go-key)]
    (merge (into {} (map (fn [x]
                    (let [comp (find-attribute-component x (keys (make-concrete-template go-key)))]
                      [x (get-attribute-sql-value comp x)])) attr-list))
           (first attr-map))))

(defn co [co-key & attr-map]
  (let [attr-list (component-attribute-keys co-key)]
    (merge (into {} (map (fn [x]
                           [x (get-attribute-sql-value co-key x)])
                         attr-list))
           (first attr-map))))
