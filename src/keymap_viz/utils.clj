(ns keymap-viz.utils
  (:require
   [keymap-viz.config :as conf]))

(defn vector-sum
  "Returns the sum of one or more equal-sized numeric vectors."
  ([x] x)
  ([x y]
   (assert (= (count x) (count y)))
   (vec (map + x y)))
  ([x y & more]
   (reduce vector-sum (concat [x y] more))))

(defmulti calc-key-corner (fn [key _] (:type key)))

(defmethod calc-key-corner :rect [key block-pos]
  (let [{:keys [layer-margin grid-size-horz grid-size-vert]} conf/config
        pos (vector-sum block-pos (:pos key))
        size (:size key)
        x (+ layer-margin (* grid-size-horz (+ size (first pos))))
        y (+ layer-margin (* grid-size-vert (inc (second pos))))]
    [x y]))

(defn calc-board-size [layout]
  (let [{:keys [layer-margin]} conf/config
        key-corners (->> (:blocks layout)
                         vals
                         (map (fn [{:keys [keys pos]}]
                                (map calc-key-corner keys (repeat pos))))
                         (apply concat))
        xmax (first (apply max-key first key-corners))
        ymax (second (apply max-key second key-corners))]
    (mapv #(+ layer-margin %) [xmax ymax])))

(defn calc-keymap-size [keymap layout]
  (let [n-layers (count (:layers keymap))
        [x y] (calc-board-size layout)]
    [x (* y n-layers)]))
