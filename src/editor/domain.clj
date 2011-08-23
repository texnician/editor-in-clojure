(ns editor.domain
  (:use [clojure.xml :as xml]))

(defmacro defdomain [name]
  (let [domain-name (symbol (str name "-domain"))
        domain-name->xml (symbol (str name "-domain->xml"))
        register-name (symbol (str "register-" name))]
    `(do (defn ~domain-name [] {})
         (defn ~domain-name->xml []
           (xml/emit {:tag ~(keyword domain-name)
                       :content (into [] (vals (~domain-name)))}))
         (defn ~register-name [~'key ~'val]
           (let [~'new-domain (assoc (~domain-name) ~'key ~'val)]
             (def ~domain-name (fn [] ~'new-domain)))))))

(defdomain go-component)
(defdomain global-enum)