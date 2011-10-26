(ns editor.sql
  (:use (editor name-util core types component sid template))
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
    (println acc)
    (if (nil? x)
      acc
      (recur (cons (f (first x)) acc) (next x)))))

(defn- translate-newline [token]
  (if (string? token)
    (string/replace token "\n" " ")
    token))

(defn- filter-args [sql-seq]
  (filter #(symbol? %) sql-seq))

(defn- build-arg-table [arg-spec obj-list]
  "输入arg specific, 返回一个生成 argument table 
'{:player {:access-from :go :type :go :attrs [:name :status :ptid :digit-id]}
  :table {:type :string}}'
 的函数"
  (if (nil? obj-list)
    {}
    (let [acc (build-arg-table arg-spec (next obj-list))]
      (if-let [[_, obj, attr] (re-matches #"(.+)\.(.+)" (-> obj-list first name))]
        (let [obj-key (keyword obj)
              t (arg-spec obj-key)]
          (println acc)
          (if (contains? acc obj-key)
            (assoc acc obj-key (assoc (acc obj-key) :attrs
                                      (conj ((acc obj-key) :attrs) (keyword attr))))
            (assoc acc obj-key {:access-from t :type t :attrs [(keyword attr)]})))
        (let [atom-key (-> obj-list first keyword)]
          (assoc acc atom-key {:type (arg-spec atom-key)}))))))


(defn create-sql [func-name arg-spec sql-seq]
  (map translate-newline sql-seq)
  (build-arg-table arg-spec (filter-args sql-seq)))

(defsql create-role [:go player, :co a :co b :string table]
  "INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(" player.name, player.status, player.ptid, player.digit-id,
player.scence-id, player.pos-x "1, 0, now(), now(), 1, 0) WHERE size between" a.attr-1 "and" b.attr-2)

{:player {:access-from :go :type :go :attrs [:name :status :ptid :digit-id]}
 :table {:type :string}}
{:func-name 'create-role
 :args {:player {:type :go} :table {:type :string}}
 :body '("INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(" player.name, player.status, player.ptid, player.digit-id,
player.scence-id, player.pos-x "1, 0, now(), now(), 1, 0)")}
