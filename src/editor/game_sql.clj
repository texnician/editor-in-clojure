(ns editor.game-sql
  (:use (editor sql)))

(defsql test-sql [:string table]
  "测试命令"
  ["SELECT * from" table])

(defsql test-sql-no-arg []
  "测试没参数的命令"
  ["SELECT * from role_info"])

(defsql test-sql-no-doc []
  "Ohaha"
  ["A sql without doc"])
