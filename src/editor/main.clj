(ns editor.main
  (:use (clojure pprint))
  (:use (editor core domain go-template domain-manager inspect-util st))
  (:gen-class))

(defn -main [& args]
  (let [files (map #(str %) args)]
    (doall (map load-file files)))
  (all-meta-component->xml-file)
  (all-go-template->xml-file)
  (all-go-domain->xml-file))
