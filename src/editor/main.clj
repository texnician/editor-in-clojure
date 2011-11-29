(ns editor.main
  (:use (clojure pprint))
  (:use (editor core domain go-component go-template domain-manager inspect-util st game-sql))
  (:gen-class))

(defn compile-xml [& files]
  (doseq [f files]
    (load-file f)
    (output-domian-to-xml-file (keyword (filename->domain-name f)))))

(declare *opt-table*)

(defn usage [& args]
  (println "Welcome to happy world editor.")
  (println "Usage: java -c editor-x.x.x-.jar [option] [arg] ...")
  (doseq [[k v] *opt-table*]
    (println (format "   %s    %s" (name k) (:doc v)))))

(defn list-components [& args]
  (let [comp-list (keys (get-domain :meta-go-component))]
    (doseq [c comp-list]
      (-> c name println))))

(defn list-templates [& args]
  (let [temp-list (keys (get-domain :go-template))]
    (doseq [t temp-list]
      (-> t name println))))

(defn list-domains [& args]
  (let [domain-list (keys (deref *global-domain*))]
    (doseq [d domain-list]
      (-> d name println))))

(defn inspect-template-by-name [arg]
  (inspect-template-fn (keyword arg)))

(defn generate-cpp [& args]
  (load-file "src/editor/go_enum.clj")
  (load-file "src/editor/go_component.clj")
  (load-file "src/editor/go_template.clj")
  (load-file "src/editor/game_sql.clj")
  (let [comp-list (keys (get-domain :meta-go-component))
        go-list (keys (get-domain :go-template))
        sql-list (keys (get-domain :sql))]
    (doseq [c comp-list]
      (gen-component c))
    (println "gen-component-define-cpp comp-list")
    (gen-component-define-cpp comp-list)
    (println "gen-component-swig-wrapper")
    (gen-component-swig-wrapper comp-list)
    (println "gen-component-factory comp-list")
    (gen-component-factory comp-list)
    (println "gen-component-factory-test comp-list")
    (gen-component-factory-test comp-list)
    (println "gen-game-object-factory comp-list")
    (gen-game-object-factory go-list)
    (println "gen-gameobject-db comp-list")
    (gen-game-object-db go-list)
    (println "gen-sql-class comp-list")
    (gen-sql-class sql-list)
    (gen-python-component comp-list)
    (gen-python-component-factory comp-list)
    (gen-python-game-object-factory go-list)))

(def *opt-table*
  {:-c {:func compile-xml :doc "Compile domain scripts, output xml data files."}
   :-C {:func generate-cpp :doc "Generate cpp srcs."}
   :-d {:func list-domains :doc "List all domains."}
   :-h {:func usage :doc "Print this message."}
   :-i {:func inspect-template-by-name :doc "Inspect template"}
   :-l {:func list-components :doc "List all components"}
   :-L {:func list-templates :doc "List all templates"}})

(defn parse-args [arg-list]
  (let [opt (first arg-list)
        args (next arg-list)]
    (fn []
      (apply (:func (*opt-table* (keyword opt))) args))))

(defn -main [& args]
  (try (let [f (parse-args args)]
         (f))
       (catch Exception e (binding [*out* *err*]
                            (println (.getMessage e))
                            (usage)))))
