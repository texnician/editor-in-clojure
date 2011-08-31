(ns editor.st
  (:import (org.stringtemplate.v4 ST
                                  STGroup
                                  STGroupDir
                                  STGroupFile
                                  STGroupString
                                  AttributeRenderer))
  (:import (java.io File))
  (:import (java.util Formatter
                      Locale)))

(let [hello (ST. "Hello, <name>")]
  (.add hello "name" "World")
  (println (.render hello)))


(let [group (STGroupDir. "./src/editor/tmp")
      st (.getInstanceOf group "decl")]
  (doto st
    (.add "type" "int")
    (.add "name" "x")
    (.add "value" 10))
  (.render st))

(let [group (STGroupFile. "./src/editor/tmp/test.stg")
      st (.getInstanceOf group "decl")]
  (doto st
    (.add "type" "int")
    (.add "name" "x")
    (.add "value" 0))
  (.render st))

(defn make-user [id name]
  (proxy [Object] []
    (getId []
      1)
    (getName []
      2)))
(macroexpand-1 '(proxy [Object] []
    (getId []
      1)
    (getName []
      2)))
;ST st = new ST("<b>$u.id$</b>: $u.name$", '$', '$');
;st.add("u", new User(999, "parrt"));
;;;String result = st.render(); // "<b>999</b>: parrt"
;; (let [st (ST. "<b>$u.getId()$</b>: $u.getName()$" \$ \$)]
;;   (.add st "u" (proxy [Object] []
;;                  (getId [] 10)
;;                  (getName [] "parrt")))
;;   (.render st))
;; (.getId (make-user 999 "parrt"))

;; ST st = new ST("<items:{it|<it.id>: <it.lastName>, <it.firstName>\n}>");
;; st.addAggr("items.{ firstName ,lastName, id }", "Ter", "Parr", 99); // add() uses varargs
;; st.addAggr("items.{firstName, lastName ,id}", "Tom", "Burns", 34);
;; String expecting =
;;         "99: Parr, Ter"+newline +
;;         "34: Burns, Tom"+newline;

(st-aggr "_: _ _" [{:id "99" :first-name "Ter" :last-name "Parr"}])

(let [st (ST. "<items:{it|<it.id>: <it.lastName>, <it.firstName>\n}>")]
  (doto st
    (.addAggr "items.{ firstName, lastName, id }" (to-array ["Ter" "Parr" 99]) )
    (.addAggr "items.{ firstName, lastName, id }" (to-array ["Tom" "Burns" 34])))
  (.render st))
(type ST)

;; String template =
;;     "foo(x,y) ::= << <x; format=\"%,d\"> <y; format=\"%,2.3f\"> >>\n";
;; STGroup g = new STGroupString(template);
;; g.registerRenderer(Number.class, new NumberRenderer());
;; ST st = group.getInstanceOf("foo");
;; st.add("x", -2100); 
;; st.add("y", 3.14159);
;; String result = st.render(new Locale("pl"));
(def *st-template* "foo(x,y) ::= << <x; format=\",%d\"> <y; format=\"%,2.3f\"> >>\n")

(defn make-rendier []
  (proxy [AttributeRenderer] []
    (toString [o, format-str, locale]
      ;(println o)
      ;(println format-str)
      ;(println locale)
      (if format-str
        (let [f (Formatter. locale)]
          (.format f format-str (to-array [o]))
          (.toString f))
        (.toString o)))))

(let [g (STGroupString. *st-template*)]
  (.registerRenderer g Number (make-rendier))
  (let [st (.getInstanceOf g "foo")]
    (doto st
      (.add "x" -2100)
      (.add "y" 3.14159))
    (.render st (Locale. "pl"))))
(bean Formatter)
(.format (Formatter. (Locale. "pl")) "%4$2s %3$2s %2$2s %1$2s", (to-array [ "a", "b", "c", "d"]))