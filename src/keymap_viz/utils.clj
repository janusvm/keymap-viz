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

(defmulti calc-key-corner :type)

(defmethod calc-key-corner :rect [key]
  (let [{:keys [layer-margin grid-size-horz grid-size-vert]} conf/config
        {:keys [size pos]}key
        x (+ layer-margin (* grid-size-horz (+ size (first pos))))
        y (+ layer-margin (* grid-size-vert (inc (second pos))))]
    [x y]))

;; TODO: this needs to take block-pos into account
(defn calc-board-size [layout]
  (let [{:keys [layer-margin]} conf/config
        key-corners (->> (:blocks layout)
                         (vals)
                         (map :keys)
                         (apply concat)
                         (map calc-key-corner))
        xmax (first (apply max-key first key-corners))
        ymax (second (apply max-key second key-corners))]
    (mapv #(+ layer-margin %) [xmax ymax])))
