(ns editor.component
  (:use (editor domain)))

(defmacro defcomponent [component & body]
  `(register-go-component ))