(ns keymap-viz.utils)

(defn vector-sum
  "Returns the sum of one or more equal-sized numeric vectors."
  ([x] x)
  ([x y]
   (assert (= (count x) (count y)))
   (vec (map + x y)))
  ([x y & more]
   (reduce vector-sum (concat [x y] more))))
