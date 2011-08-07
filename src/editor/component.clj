(ns component)
(defmacro defcomponent [component & body]
  (declare)
  `'(~(str component)))