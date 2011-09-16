(ns editor.domain-manager
  (:use (editor domain go-template go-component))
  (:require [clojure.string :as string])
  (:import (java.io File)))

;;; default xml output dir
(def *xml-output-dir* ".")

(defn get-all-go-domain-name []
  "返回所有的game object keyword列表"
  (keys (get-domain :go-template)))

(defn domain-name->filename [dir-name suffix]
  (fn [name-key]
    (let [dir (File. dir-name)
          path (.getAbsolutePath dir)]
      (apply str (concat path File/separator (name name-key) "." suffix)))))

(defn filename->domain-name [filename]
  (let [f (File. filename)
        tokens (string/split (string/replace (.getName f) #"_" "-") #"\.")]
    (apply str (take (-> tokens count dec) tokens))))

(defn output-domian-to-xml-file [domain-key]
  (let [dir (File. *xml-output-dir*)]
    (if-not (.exists dir)
      (.mkdirs dir))
    (domain->xml-file ((domain-name->filename (.getAbsolutePath dir) "xml") domain-key) domain-key)))

(defn all-go-domain->xml-file []
  (doall (map output-domian-to-xml-file (get-all-go-domain-name))))


(defn all-go-template->xml-file []
  (output-domian-to-xml-file :go-template))

(defn all-meta-component->xml-file []
  (output-domian-to-xml-file :meta-go-component))