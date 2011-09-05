(ns editor.error
  (:use (editor core))
  (:require [clojure.string :as string]))

(def *error-table* (atom {}))

(declare *error-table*)
(defmacro deferror [error-name code fmtstr]
  (let [pattern #"<([\w-]+)>"
        fmt-args (map #(-> % second keyword) (re-seq pattern fmtstr))]
    `(swap! *error-table* (fn [~'t]
                            (assoc ~'t ~error-name
                                   {:error-name ~error-name
                                    :code ~code
                                    :msg-fn (fn [~(into {} (map (fn [x]
                                                                  [(symbol (name x)) x])
                                                                fmt-args))]
                                              (format ~(string/replace fmtstr pattern "%s")
                                                      ~@(map #(-> % name symbol) fmt-args)))})))))

(defn emit-error [err args]
  (let [e (-> (deref *error-table*) err)]
    (throw (Exception. (format "error<%d> %s" (:code e) ((:msg-fn e) args))))))

(deferror :attribute-not-exist 1001 "属性'<attr>'不存在")

(string/split "abc;def;ghi" #";")
(string/join \| (string/split "abc;def;ghi" #";"))
("key-value")