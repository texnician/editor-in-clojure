(ns editor.error
  (:use (editor core))
  (:require [clojure.string :as string]))

(def *error-table* (atom {}))

(declare *error-table*)

(defmacro deferror [error-name code fmtstr]
  (let [pattern #"<([\w-]+)>"
        fmt-args (map #(-> % second keyword) (re-seq pattern fmtstr))
        arg-list (map #(gensym (str (name %) "__")) fmt-args)]
    `(swap! *error-table* (fn [t#]
                            (assoc t# ~error-name
                                   {:error-name ~error-name
                                    :code ~code
                                    :msg-fn (fn [~(into {} (map vector arg-list fmt-args))]
                                              (format ~(string/replace fmtstr pattern "%s")
                                                      ~@arg-list))})))))

(defn emit-error [err args]
  (let [etb (-> (deref *error-table*) err)]
    (throw (Exception. (format "error<%d> %s" (:code etb) ((:msg-fn etb) args))))))

(deferror :attribute-not-exist 1001 "属性'<attr>'不存在")
(deferror :array-attribute-value-not-vector 1002 "属性'<attr>'的值'<value>'不是vector")
