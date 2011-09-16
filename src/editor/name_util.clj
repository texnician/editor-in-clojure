(ns editor.name-util
  (:require [clojure.string :as string]))

(defn split-clojure-token [token]
  "Split clojure token to words"
  (string/split (name token) #"-"))

(defn clojure-token->cpp-token [key]
  "Convert clojure symbol name to valid c++ token aaa-bbb => AaaBbb"
  (let [tokens (split-clojure-token key)]
    (apply str (map #(string/capitalize %) tokens))))

(defn clojure-token->cpp-enum-token [key]
  "Convert clojure symbol name to valid c++ enum token"
  (let [tokens (split-clojure-token key)]
    (string/join \_ (map #(string/upper-case %) tokens))))

(defn clojure-token->cpp-variable-token [key]
  "Convert clojure symbol name to valid c++ variable token aaa-bbb = > aaa_bbb"
  (let [tokens (split-clojure-token key)]
    (string/join \_ tokens)))

(defn cpp-component-name [comp-key]
  "Return c++ component name"
  (format "%sComponent" (clojure-token->cpp-token comp-key)))

(defn cpp-component-gen-name [comp-key]
  (format "%sGen" (cpp-component-name comp-key)))

(defn cpp-component-factory-name [comp-key]
  "Return c++ component factory name"
  (format "%sComponentFactory" (clojure-token->cpp-token comp-key)))

(defn cpp-component-header-filename [comp-key]
  "Return c++ component factory header filename"
  (format "%s_component.h" (clojure-token->cpp-variable-token comp-key)))

(defn cpp-component-gen-header-filename [comp-key]
  (format "%s_component_gen.h" (clojure-token->cpp-variable-token comp-key)))

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

(defn cpp-component-gen-header-define [comp-key]
  (let [tokens (split-clojure-token comp-key)]
     (format "_%s_COMPONENT_GEN_H_" (string/join \_ (map #(string/upper-case %) tokens)))))

(defn cpp-component-factory-test-filename []
  "component_factory_test")

(defn cpp-component-sid-initialize-filename []
  "component_gen_initialize.cpp")