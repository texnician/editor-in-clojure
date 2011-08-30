(ns editor.main
  (:use (clojure pprint))
  (:use (editor core domain go-template))
  (:gen-class))

(defn -main [& args]
  (println "Welcome to happy world editor! These are your args:" args)
  (let [files (map #(str %) args)]
    (doall (map load-file files)))
  (domain->xml-file "fruit.xml" :fruit)
  (domain->xml-file "seed.xml" :seed)
  (print1 (get-domain :fruit))
  (print1 (get-domain :seed)))
