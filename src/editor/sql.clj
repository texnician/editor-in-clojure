(ns editor.sql
  (:use (editor name-util core types component sid template))
  (:require [clojure.string :as string]))

(declare create-sql)
(declare parse-arg-spec)
(defmacro defsql [func-name arg-spec & body]
  `(create-sql '~func-name ~(parse-arg-spec arg-spec) '~body))

(defn- parse-arg-spec [arg-spc]
  (let [spec-map (apply array-map '[:go player, :string table])]
    (zipmap (map #(keyword %) (vals spec-map))
            (map #(array-map :type %) (keys spec-map)))))


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

(defn- filter-variables [sql-seq]
  (filter #(symbol? %) sql-seq))

(defn- filter-game-object [sql-seq])
(defn create-sql [func-name args sql-seq]
  (map translate-newline sql-seq)
  (filter-variables sql-seq))

(defsql create-role [:go player, :string table]
  "INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(" player.name, player.status, player.ptid, player.digit-id,
player.scence-id, player.pos-x "1, 0, now(), now(), 1, 0)")

{:func-name 'create-role
 :args {:player {:type :go} :table {:type :string}}
 :body '("INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(" player.name, player.status, player.ptid, player.digit-id,
player.scence-id, player.pos-x "1, 0, now(), now(), 1, 0)")}
