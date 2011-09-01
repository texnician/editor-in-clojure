(ns editor.main
  (:use (clojure pprint))
  (:use (editor core domain go-template domain-manager inspect-util))
  (:gen-class))

(defn -main [& args]
  (println "Welcome to happy world editor! These are your args:" args)
  (let [files (map #(str %) args)]
    (doall (map load-file files)))
  (print1 (get-domain :fruit))
  (print1 (get-domain :seed))
  (all-meta-component->xml-file)
  (all-go-template->xml-file)
  (all-go-domain->xml-file))
