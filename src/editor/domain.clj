(ns editor.domain)

(defmacro defdomain [name]
  (let [domain-name (symbol (str name "-domain"))
        register-name (symbol (str "register-" name))]
    `(do (defn ~domain-name [] {})
         (defn ~register-name [~'key ~'val]
           (let [~'new-domain (assoc (~domain-name) ~'key ~'val)]
             (def ~domain-name (fn [] ~'new-domain)))))))

(defdomain go-component)
(defdomain go-template)
