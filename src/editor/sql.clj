(ns editor.sql
  (:use (editor name-util core types component sid template domain error))
  (:require [clojure.string :as string]))

(declare create-sql)
(declare parse-arg-spec)

(defmacro defsql [func-name arg-spec & body]
  `(create-sql '~func-name ~(parse-arg-spec arg-spec) '~body))

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
  (if (nil? obj-list)
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

(defmethod sql-placeholder->fmt-string :c [lang]
  (fn [arg-table sym]
    (*c-sql-string-fmt-table* (if-let [[_, obj, attr] (split-object-token (-> sym name))]
                                (let [obj-key (keyword obj)
                                      attr-key (keyword attr)]
                                  (attr-type attr-key))
                                (let [atom-key (-> sym keyword)]
                                  (arg-type atom-key arg-table))))))

(defn- translate-fmt-string [lang arg-table]
  (fn [token]
    (if (symbol? token)
      ((sql-placeholder->fmt-string lang) arg-table token)
      token)))

(defn- make-full-sql-fmt-string [lang arg-table sql-template]
  (string/replace (string/replace (string/join \space (map (translate-fmt-string lang arg-table) sql-template))
                                  #"  +" " ") #"\s," ","))

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

(defn- make-target-arg-list [lang arg-table sql-template]
  (let [arg-sym-list (filter-args sql-template)
        maker (target-arg-list-maker lang arg-table)]
    (map maker arg-sym-list)))

;;; player["role-title"].asCstring()
(defn create-sql [func-name arg-spec sql-seq]
  (let [sql-template (map (comp translate-token translate-newline translate-separator) sql-seq)
        arg-table (build-arg-table arg-spec (filter-args sql-template))]
    {:fmt-str (make-full-sql-fmt-string :c arg-table sql-template)
     :arg-list (make-target-arg-list :c arg-table sql-template)}))

(defn test-create-role [player table]
  (format "%s" (player :name)
          table
          (player :id)
          (player :role-title)
          (player :long-id)))

(defsql create-role [:go player, :string table]
  "INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(3, " player.name | player.id | player.role-title | player.long-id |
player.level | player.role-logic ", 1, 0, now(), now(), 1, 0) WHERE size between" "and")

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