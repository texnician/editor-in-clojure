(ns editor.game-sql
  (:use (editor sql go-component)))

(defsql create-role [:go player, :string table :go item]
  "创建玩家的命令"
  ["INSERT into" table "(role_name, role_status, ptid, digit_id,
 scence_id, pos_x, pos_y, career, sex, dir, face, face_color,
 hair_type, hair_color, grade, level, create_time, update_time,
extra_state, role_deleted) 
VALUES(3, " player.name | player.id | player.role-title | player.long-id | item.is-tradable
| player.level | player.role-logic ", 1, 0, now(), now(), 1, 0) WHERE size between" "and"])

(defsql test-sql [:string table]
  "测试命令"
  ["SELECT * from" table])

(defsql test-sql-no-arg []
  "测试没参数的命令"
  ["SELECT * from role_info"])

(defsql test-sql-no-doc []
  ["A sql without doc"])