(ns editor.main
  (:use (clojure pprint))
  (:use (editor core domain go-template domain-manager))
  (:gen-class))

(defn -main [& args]
  (println "Welcome to happy world compiler! These are your args:" args)
  (let [files (map #(str %) args)]
    (doall (map load-file files)))
  (all-meta-component->xml-file)
  (all-go-template->xml-file)
  (all-go-domain->xml-file))
