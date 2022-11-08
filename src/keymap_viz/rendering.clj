(ns keymap-viz.rendering
  (:require
   [hiccup.core :as h]
   [keymap-viz.config :as conf]
   [keymap-viz.utils :as u]))

;; HELPER FUNCTIONS ------------------------------------------------------------
(defmulti keymap-key->keymap-opts class)
(defmethod keymap-key->keymap-opts String [k] {:tap k})
(defmethod keymap-key->keymap-opts clojure.lang.PersistentArrayMap [m] m)

(defn make-key
  "Construct a key map with all the data necessary for rendering"
  [keymap-key layout-opts block-pos]
  (let [{:keys [type pos size]} layout-opts
        keymap-opts (keymap-key->keymap-opts keymap-key)
        {:keys [tap hold]} keymap-opts
        key-pos (u/vector-sum pos block-pos)]
    {:type type :size size :pos key-pos :tap tap :hold hold}))

;; RENDERING FUNCTIONS ---------------------------------------------------------
(defmulti render-key
  "Translate a key config map into Hiccup data."
  :type)

(defmethod render-key :rect render-key-rect [key]
  ;; TODO: add support for multiword texts and Hold texts
  (let [{:keys [key-padding border-radius grid-size-horz grid-size-vert layer-margin]} conf/config
        {:keys [size pos tap hold]} key
        rect-x (+ layer-margin key-padding (* grid-size-horz (first pos)))
        rect-y (+ layer-margin key-padding (* grid-size-vert (second pos)))
        text-x (+ layer-margin (* grid-size-horz (+ (first pos) (* 0.5 size))))
        text-y (+ layer-margin (* grid-size-vert (+ (second pos) 0.5)))]
    [[:rect {:x rect-x :y rect-y
             :rx border-radius :ry border-radius
             :width (- (* size grid-size-horz) (* 2 key-padding))
             :height (- grid-size-vert (* 2 key-padding))}]
     [:text {:x text-x :y text-y} (h/h tap)]]))

(defn render-block
  "Translate a keymap block into a vector of Hiccup elements."
  [keymap-block layout-block]
  (let [block-pos (:pos layout-block)
        layout-keys (:keys layout-block)]
    (->> (map make-key keymap-block layout-keys (repeat block-pos))
         (map render-key)
         (apply concat)
         (into []))))

(defn render-layer
  "Translate a single keymap layers into a vector of rendered blocks."
  [keymap-layer layout]
  (let [keymap (:keymap keymap-layer)
        block-layouts (:blocks layout)
        block-keys (keys block-layouts)]
    (->> (for [k block-keys]
           (render-block (k keymap) (k block-layouts)))
         (apply concat)
         (into []))))

(defn render-keymap
  "Translate a keymap definition into Hiccup data."
  [keymap layout]
  (let [keymap-layers (:layers keymap)]
    (->> (map render-layer keymap-layers (repeat layout))
         (apply concat)
         (into []))))

(defn render-svg
  "Create an SVG element as Hiccup data."
  [& elements]
  ;; TODO: make hardcoded values configurable/inferred from config
  (let [{:keys [layer-margin grid-size-horz grid-size-vert]} conf/config
        board-size (u/calc-board-size conf/layout)
        w (first board-size)
        h (second board-size)
        viewbox-str (str "0 0 " w " " h)
        xmlns "http://www.w3.org/2000/svg"]
    (-> [:svg {:width w :height h :viewBox viewbox-str :xmlns xmlns} [:style conf/default-style]]
        (concat elements)
        (vec))))
