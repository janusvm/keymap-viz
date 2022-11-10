(ns keymap-viz.rendering
  (:require
   [hiccup.core :as h]
   [keymap-viz.config :as conf]
   [keymap-viz.utils :as u]))

;; HELPER FUNCTIONS ------------------------------------------------------------
(defmulti key-labels->key-opts class)
(defmethod key-labels->key-opts nil [_] {:tap ""})
(defmethod key-labels->key-opts Number [n] {:tap n})
(defmethod key-labels->key-opts Character [c] {:tap c})
(defmethod key-labels->key-opts String [s] {:tap s})
(defmethod key-labels->key-opts clojure.lang.PersistentArrayMap [m] m)

(defn make-key
  "Construct a key map with all the data necessary for rendering"
  [keymap-key layout-opts block-pos]
  (let [{:keys [type pos size]} layout-opts
        keymap-opts (key-labels->key-opts keymap-key)
        {:keys [tap hold]} keymap-opts
        key-pos (u/vector-sum pos block-pos)]
    {:type type :size size :pos key-pos :tap (str tap) :hold (str hold)}))

;; RENDERING FUNCTIONS ---------------------------------------------------------
(defmulti render-key
  "Translate a key config map into Hiccup data."
  (fn [key _]
    (:type key)))

(defmethod render-key :rect render-key-rect [key yshift]
  ;; TODO: add support for multiword texts and Hold texts
  (let [{:keys [key-padding border-radius grid-size-horz grid-size-vert layer-margin]} conf/config
        {:keys [size pos tap hold]} key
        rect-x (+ layer-margin key-padding (* grid-size-horz (first pos)))
        rect-y (+ layer-margin key-padding (* grid-size-vert (second pos)) yshift)
        text-x (+ layer-margin (* grid-size-horz (+ (first pos) (* 0.5 size))))
        text-y (+ layer-margin (* grid-size-vert (+ (second pos) 0.5)) yshift)]
    [[:rect {:x rect-x :y rect-y
             :rx border-radius :ry border-radius
             :width (- (* size grid-size-horz) (* 2 key-padding))
             :height (- grid-size-vert (* 2 key-padding))}]
     [:text {:x text-x :y text-y} (h/h tap)]]))

(defn render-block
  "Translate a keymap block into a vector of Hiccup elements."
  [keymap-block layout-block yshift]
  (let [block-pos (:pos layout-block)
        layout-keys (:keys layout-block)
        keys (map make-key keymap-block layout-keys (repeat block-pos))]
    (->> (map render-key keys (repeat yshift))
         (apply concat)
         (into []))))

(defn render-layer
  "Translate a single keymap layers into a vector of rendered blocks."
  [keymap-layer layout yshift]
  (let [keymap (:keymap keymap-layer)
        block-layouts (:blocks layout)
        block-keys (keys block-layouts)]
    (->> (for [k block-keys]
           (render-block (k keymap) (k block-layouts) yshift))
         (apply concat)
         (into []))))

(defn render-keymap
  "Translate a keymap definition into Hiccup data."
  [keymap layout]
  (let [keymap-layers (:layers keymap)
        [_ yshift] (u/calc-board-size layout)]
    (->> (iterate (partial + yshift) 0)
         (map render-layer keymap-layers (repeat layout))
         (apply concat)
         (into []))))

(defn render-svg
  "Create an SVG element as Hiccup data."
  [size & elements]
  ;; TODO: make hardcoded values configurable/inferred from config
  (let [{:keys [layer-margin grid-size-horz grid-size-vert]} conf/config
        w (first size)
        h (second size)
        viewbox-str (str "0 0 " w " " h)
        xmlns "http://www.w3.org/2000/svg"]
    (-> [:svg {:width w :height h :viewBox viewbox-str :xmlns xmlns} [:style conf/default-style]]
        (concat elements)
        (vec))))
