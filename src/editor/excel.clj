(ns editor.excel
  (:use (dk.ative.docjure spreadsheet))
  (:require [clojure.string :as string]))

; Load a spreadsheet and read the first two columns from the 
; price list sheet:

(defn- list->number-string [l]
  (string/join ", " l))

(defn- define-str [d]
  (format "EXP_SYS[%d] = [%s]" (:id d) (:str d)))

(defn- gen-exp-system [file]
  (let [step1-seq (map (fn [n]
                           (let [exp-seq (->> (load-workbook file)
                                              (select-sheet (str "经验体系" n))
                                              (select-columns {:A :level, :B :exp}))]
                             {:id n :data (filter #(number? (:exp %)) exp-seq)}))
                         (range 1 14))]
    (let [step2-seq (map (fn [m]
                           {:id (:id m) :data (map (fn [x]
                                                     (-> x :exp Math/round))
                                                   (:data m))})
                         step1-seq)]
      (let [step3-seq (map (fn [x]
                             {:id (:id x) :str (list->number-string (:data x))})
                           step2-seq)]
        (spit "out.py" (string/join "\n" (map #(define-str %) step3-seq)))))))

(gen-exp-system "exp.xlsx")
