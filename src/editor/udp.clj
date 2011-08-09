(ns udp
  (:refer-clojure :exclude [get]))

(defn beget [o p] (assoc o ::prototype p))
(beget {:sub 0} {:super 1})
(def put assoc)

(defn get [m k]
  (when m
    (if-let [[_ v] (find m k)]
      v
      (recur (::prototype m) k))))

(get (beget {:sub 0} {:super 1})
     :super)

(def cat {:likes-dogs true, :ocd-bathing true})
(def morris (beget {:likes-9lives true} cat))
(def post-traumatic-morris (beget {:likes-dogs nil} morris))

(get cat :likes-dogs)
(get morris :likes-dogs)
(get post-traumatic-morris :likes-dogs)

(defmulti compiler :os)
(defmethod compiler ::unix [m] (get m :c-compiler))