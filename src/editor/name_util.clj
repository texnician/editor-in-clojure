(ns editor.name-util
  (:require [clojure.string :as string]))

(defn split-clojure-token [token]
  "Split clojure token to words"
  (string/split (name token) #"-"))

(defn clojure-token->cpp-token [key]
  "Convert clojure symbol name to valid c++ token"
  (let [tokens (split-clojure-token key)]
    (apply str (map #(string/capitalize %) tokens))))

(defn cpp-component-name [comp-key]
  "Return c++ component name"
  (format "%sComponent" (clojure-token->cpp-token comp-key)))

(defn cpp-getter-name [attr-key]
  "Return c++ getter name"
  (format "Get%s" (clojure-token->cpp-token attr-key)))

(defn cpp-setter-name [attr-key]
  "Return c++ setter name"
  (format "Set%s" (clojure-token->cpp-token attr-key)))

(defn cpp-component-header-define [comp-key]
  "Return c++ header guard define name"
  (let [tokens (split-clojure-token comp-key)]
    (format "_%s_COMPONENT_H_" (string/join \_ (map #(string/upper-case %) tokens)))))

(cpp-getter-name :id)
(cpp-component-name :base)