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
(defmethod compiler ::osx [m] (get m :c-compiler))

(def clone (partial beget {}))
(def unix {:os ::unix :c-compiler "cc" :home "/home" :dev "/dev"})
(def osx (-> (clone unix) (put :os ::osx)
             (put :c-compiler "gcc")
             (put :home "/Users")))

(compiler unix)
(compiler osx)

(defmulti home :os)
(defmethod home ::unix [m] (get m :home))
(home unix)
(home osx)
(derive ::osx ::unix)
(parents ::osx)
(ancestors ::osx)
(isa? ::osx ::unix)

(derive ::osx ::bsd)
(defmethod home ::bsd [m] "/home")

(home osx)

(prefer-method home ::unix ::bsd)
(home osx)

(remove-method home ::unix)
(remove-method home ::bsd)
(home osx)