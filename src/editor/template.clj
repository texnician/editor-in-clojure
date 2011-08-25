(ns editor.template
  (:use (editor component))
  (:use (editor domain)))


(deftemplate fruit
  "果实的模板"
  (base :name "未命名的果实")
  (item-base :item-lifetime 10)
  (trade))

(make-template-node :fruit "果实的模板" '(:base)  '(:item-base {:item-lifetime 10}) '(:trade))

(defn make-template-node [temp-key doc & components]
  {:tag :go-template
   :attrs {:name (name temp-key) :doc doc}
   :content 
   (if (not (nil? components))
      (apply vector (map (fn [entry]
                           (apply make-component-node entry)) components))
      nil)})

(make-template-node :fruit "" '(a))

(defmacro deftemplate [name & body]
  (let [temp-key (keyword name)
        [doc & comp-list] (if (string? (first body))
                            body
                            (conj body ""))]
    `(register-go-template ~temp-key (make-template-node ~temp-key ~doc
                                                         ~@(map (fn [x]
                                                                  `'~(conj (if-let [attr-vals (next x)]
                                                                             (list (apply array-map attr-vals)) 
                                                                             nil) (keyword (first x)))) comp-list)))))