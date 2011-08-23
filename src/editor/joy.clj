(for [x (range 10)]
  (println x))
(defn reverse-list [l]
  (loop [acc nil x l]
    (if (seq x)
      (recur (cons (first x) acc) (rest x))
      acc)))
(reverse-list (range 10))
(defn xors [max-x max-y]
  (for [x (range max-x) y (range max-y)]
    [x y (rem (bit-xor x y) 256)]))
(xors 2 2)
(def frame (java.awt.Frame. "Image Frame"))
(doto frame
  (.setSize (java.awt.Dimension. 200 200))
  (.setVisible true))
(def gfx (.getGraphics frame))
(doto gfx
  (.setColor (java.awt.Color. 255 128 0))
  (.fillRect 100 100 50 75))

(doseq [[x y xor] (xors 500 500)]
  (doto gfx
    (.setColor (java.awt.Color. xor xor xor))
    (.fillRect y x 1 1)))

(defn clear [g] (.clearRect g 0 0 256 256))

(defn f-values [f xs ys]
  (for [x (range xs) y (range ys)]
    [x y (rem (f x y) 256)]))

(defn draw-values [f xs ys]
  (clear gfx)
  (.setSize frame (java.awt.Dimension. xs ys))
  (doseq [[x y v] (f-values f xs ys)]
    (.setColor gfx (java.awt.Color. v v v))
    (.fillRect gfx x y 1 1)))

(draw-values bit-xor 256 256)
(draw-values bit-and 256 256)
(draw-values bit-or 256 256)
(draw-values + 256 256)
(draw-values * 256 256)
(defn foo [x]
  (let [a 1 b 2]
    (+ x a b)))

(every? (fn [x]
          (> x 0)) [0 1 -1])
(defn neighbors
  ([size yx] (neighbors [[-1, 0] [0, 1] [1, 0] [0, -1]] size yx)) 
  ([delta size yx]
     (filter (fn [c]
               (every? (fn [x]
                         (< -1 x size)) c)) (map (fn [d]
                                                 (map + d yx)) delta))))
(def matrix
  [[1 2 3]
   [4 5 6]
   [7 8 9]])
(next '(a b c d))
(name :name)
(map #(get-in matrix %) (neighbors 3 [1 1]))

(def schedule
  (conj clojure.lang.PersistentQueue/EMPTY
        :wake-up :shower :brush-teeth))
(type (pop schedule))

(map #(vector % %) #{:a :b :c :d})
(defn index [coll]
  (cond (map? coll) (seq coll)
        (set? coll) (map #(vector % %) coll)
        :else (map vector (iterate inc 0) coll)))

(seq (array-map :NPC 0 :PLANT 1 :PLAYER 2))
(contains?  :NPC)
(index #{:a :b :c :d})
(defn pos [pred coll]
  (for [[i v] (index coll) :when (pred v)] i))
(:a {:a 1 :b 2 :c 3})
(pos #(= % 3) [:a 1 :b 2 :c 3 :d 4 3])
(pos 3 {:a 1 :b 2 :c 3 :d 3})

(defn xconj [t v]
  (cond (nil? t) {:val v :L nil :R nil}
        (< v (:val t)) {:val (:val t),
                        :L (xconj (:L t) v)
                        :R (:R t)}
        :else {:val (:val t)
               :L (:L t)
               :R (xconj (:R t) v)}))

(def tree1 (xconj nil 5))
(def tree1 (xconj tree1 3))
(def tree1 (xconj tree1 2))

(defn xseq [t]
  (when t
    (concat (xseq (:L t)) [(:val t)] (xseq (:R t)))))
(xseq tree2)

(def tree2 (xconj tree1 7))
(identical? (tree1 :L) (:L tree2))

(defn triangle [n]
  (/ (* n (+ n 1)) 2))

(defn inf-triangles [n]
  {:head (triangle n)
   :tail (delay (inf-triangles (inc n)))})

(defn head [l] (:head l))
(defn tail [l] (force (:tail l)))

(def tri-nums (inf-triangles 1))
(head tri-nums)
(head (tail tri-nums))

(defn taker [n l]
  (loop [t n, src l, acc []]
    (if (zero? t)
      acc
      (recur (dec t) (tail src) (conj acc (head src))))))

(defn nthr [l n]
  (if (zero? n)
    (head l)
    (recur (tail l) (dec n))))

(taker 10 tri-nums)
(nthr tri-nums 99)
(defn mon [n]
  (take n (repeatedly #(rand-int n))))
(mon 10)

(defn sort-parts [work]
  (lazy-seq
   (loop [[part & parts] work]
     (if-let [[pivot & xs] (seq part)]
       (let [smaller? #(< % pivot)]
         (recur (list*
                 (filter smaller? xs)
                 pivot
                 (remove smaller? xs)
                 parts)))
       (when-let [[x & parts] parts]
         (cons x (sort-parts parts)))))))

(defn qsort [xs]
  (sort-parts (list xs)))

(= (concat (list '(1 2 3) 4 '(5 6 7)) '(8 0 1))
   (list* '(1 2 3) 4 '(5 6 7) '(8 0 1)))

(vec (map keyword '[MONDAY THUESDAY WEDNESDAY THURSDAY FRIDAY SATURDAY SUNDAY]))
(take 10 (qsort (mon 100)))

(map [:chthon :phthor :beowulf :grendel] '(0 3))

(defn fnth [n]
  (apply comp (cons first (take (dec n) (repeat rest)))))

((fnth 5) (range 10))

(map (comp keyword #(.toLowerCase %) name) '[MONDAY THUESDAY WEDNESDAY THURSDAY FRIDAY SATURDAY SUNDAY])
(apply str (interpose "," '[a b c]))

(def plays [{:band "Burial", :plays 979, :loved 9}
            {:band "Eno", :plays 2333, :loved 15}
            {:band "Bill Evans", :plays 979, :loved 9}
            {:band "Magma", :plays 2665, :loved 31}])
(def sort-by-loved-ratio (partial sort-by #(/ (:plays %) (:loved %))))
(sort-by-loved-ratio plays)

(defn columns [c]
  (fn [x]
    (vec (map x c))))

(defn sort-by-columns [c]
  (partial sort-by (columns c)))

(some '#{c d e} '(b c d a))

((sort-by-columns [:plays]) plays)
((columns [:plays :loved :band]) '{:band "Burial", :plays 979, :loved 9})

;; map, reduce, filter, for, some, repeatedly, sort-by, keep
;; take-while, and drop-while