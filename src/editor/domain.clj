(ns editor.domain
  (:use [clojure.xml :as xml]))

;; (defmacro defdomain [name]
;;   (let [domain-name (symbol (str name "-domain"))
;;         domain-name->xml (symbol (str name "-domain->xml"))
;;         register-name (symbol (str "register-" name))
;;         old-ns (ns-name *ns*)]
;;     `(do (in-ns 'editor.domain)
;;          (defn ~domain-name [] {})
;;          (defn ~domain-name->xml []
;;            (xml/emit {:tag ~(keyword domain-name)
;;                        :content (into [] (vals (~domain-name)))}))
;;          (defn ~register-name [~'key ~'val]
;;            (let [~'new-domain (assoc (~domain-name) ~'key ~'val)]
;;              (def ~domain-name (fn [] ~'new-domain))))
;;          (in-ns '~old-ns)
;;          name)))

(def *global-domain* {})

(defn init-global-domain []
  (def *global-domain* {}))

(defn get-domain [name]
  (if-let [domain (*global-domain* name)]
    domain
    (do (def *global-domain* (assoc *global-domain* name {}))
        (get-domain name))))

(defn domain->xml [name-key]
  (if-let [domain (get-domain name-key)]
    (xml/emit {:tag (keyword (str (name name-key) "-domain"))
               :content (into [] (vals domain))})))

(defn domain->xml-file [filename name-key]
  (if-let [domain (get-domain name-key)]
    (spit filename (with-out-str (xml/emit {:tag (keyword (str (name name-key) "-domain"))
                                            :content (into [] (vals domain))})))))

(defn register-in-domain [name key val]
  (if-let [domain (get-domain name)]
    (def *global-domain* (assoc *global-domain* name (assoc domain key val)))))

(defn reset-domain [name]
  (def *global-domain* (assoc *global-domain* name {})))
