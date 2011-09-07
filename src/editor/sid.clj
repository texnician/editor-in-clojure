(ns editor.sid
  (:require [clojure.string :as string])
  (:import (java.lang.Math)))

(def *base* 65521)

;; "*nmax* is the larges n such that
;; (< ((fn [n]
;;       (+ (* 255 n (inc n) 1/2) (* (inc n) (inc *base*)))) 5552)  (long (dec (Math/pow 2 32))))
;; "
(def *nmax* 5552)

(def *block-size* 16)


(defn- string->int-seq [str]
  (map (comp long int) (string/lower-case str)))

(partition 8 8 (repeat 0) (range 10))

(defn gen-sid [str]
  "Generate CRC32 value of str"
  )