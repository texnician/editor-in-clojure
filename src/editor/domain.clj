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

(def *global-domain* (atom {}))

(defn init-global-domain []
  (reset! *global-domain* {}))

(defn get-domain [name]
  (if-let [domain ((deref *global-domain*) name)]
    domain
    (do (swap! *global-domain* #(assoc % name {}))
        (get-domain name))))

(defn foo [x]
  {:tag 'a
   :attrs 'b
   :content (if-not (empty? x) (str x) nil)
   })

(defn translate-xml-escape-char [{tag :tag attrs :attrs content :content}]
  (letfn [(fix-xml-string [s]
            (apply str (replace {\< "&lt;",
                                 \> "&gt;",
                                 \& "&amp;",
                                 \' "&apos;",
                                 \" "&quot;"} s)))
          (fix-attr [attr]
            (if (empty? attr)
              nil
              (into {} (map (fn [[key val]]
                              [key (if (string? val) (fix-xml-string val) val)]) attr))))]
    {:tag tag
     :attrs (fix-attr attrs)
     :content
     (cond (empty? content) nil
           ;; if string value, fix string
           (and (string? (first content)) (= 1 (count content))) [(fix-xml-string (first content))]
           ;; recursivly translate
           :else (into [] (map translate-xml-escape-char content)))}))

(defn domain->xml [name-key]
  (if-let [domain (get-domain name-key)]
    (xml/emit (translate-xml-escape-char {:tag (keyword (str (name name-key) "-domain"))
                                          :content (into [] (vals domain))}))))

(defn domain->xml-file [filename name-key]
  (if-let [domain (get-domain name-key)]
    (spit filename (with-out-str (domain->xml name-key)))))

(defn register-in-domain [name key val]
  (if-let [domain (get-domain name)]
    (swap! *global-domain* #(assoc % name (assoc domain key val)))))

(defn reset-domain [name]
  (swap! *global-domain* #(assoc % name {})))
