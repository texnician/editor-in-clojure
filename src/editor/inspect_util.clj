(ns inspect-util
  (:use (editor core template domain))
  (:require (clojure inspector)))

(defn- inspect-template-fn [name-key]
  (clojure.inspector/inspect-table (make-inspect-template name-key)))

(defmacro inspect-template [name]
  `(inspect-template-fn ~(keyword name)))

(defn- inspect-object-fn [domain-key id]
  (clojure.inspector/inspect-table (make-inspect-game-object domain-key id)))

(defmacro inspect-object [domain id]
  `(inspect-object-fn ~(keyword domain) ~id))
