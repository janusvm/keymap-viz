(ns keymap-viz.core
  (:require [clojure.string :as str])
  (:gen-class))

(def key-w 55.0)
(def key-h 45.0)
(def key-rx 6.0)
(def key-ry 6.0)
(def inner-pad-w 2.0)
(def inner-pad-h 2.0)
(def outer-pad-w (/ key-w 2))
(def outer-pad-h (/ key-h 2))
(def line-spacing 18.0)

(def keyspace-w (+ key-w (* 2 inner-pad-w)))
(def keyspace-h (+ key-h (* 2 inner-pad-h)))
(def hand-w (* 5 keyspace-w))
(def hand-h (* 4 keyspace-h))
(def layer-w (+ (* 2 hand-w) outer-pad-w))
(def layer-h hand-h)
(def board-w (+ layer-w (* 2 outer-pad-w)))
(def board-h (+ (* 1 layer-h) (* 2 outer-pad-h)))

(def style "svg {
    font-family: SFMono-Regular,Consolas,Liberation Mono,Menlo,monospace;
    font-size: 14px;
    font-kerning: normal;
    text-rendering: optimizeLegibility;
    fill: #24292e;
}
rect {
    fill: #f6f8fa;
}
.held {
    fill: #fdd;
}")

(defn held [key]
  {:key key :class "held"})

(def keymap
  [{:left [["q" "w" "f" "p" "b"]
           ["a" "r" "s" "t" "g"]
           ["z" "x" "c" "d" "v"]]
    :right [["j" "l" "u" "y" "del"]
            ["m" "n" "e" "i" "o"]
            ["k" "h" "," "." "/"]]
    :thumbs {:left ["tab" "space"]
             :right ["back space" "esc"]}}])

(defn print-key [x y key]
  (let [key-class ""
        words (str/split key #" ")]
    (println (format "<rect rx=\"%s\" ry=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"%s\" />"
                     key-rx
                     key-ry
                     (+ x inner-pad-w)
                     (+ y inner-pad-h)
                     key-w
                     key-h
                     key-class))
    (loop [y (+ y (/ (- keyspace-h (* line-spacing (dec (count words)))) 2))
           ws words]
      (if (empty? ws)
        nil
        (do
          (println (format "<text text-anchor=\"middle\" dominant-baseline=\"middle\" x=\"%s\" y=\"%s\">%s</text>"
                           (+ x (/ keyspace-w 2))
                           y
                           (first ws)))
          (recur (+ y line-spacing) (rest ws)))))))

(defn print-row [x y row]
  (loop [x x
         keys row]
    (if (empty? keys)
      nil
      (do
        (print-key x y (first keys))
        (recur (+ x keyspace-w) (rest keys))))))

(defn print-block [x y block]
  (loop [y y
         rows block]
    (if (empty? rows)
      nil
      (do
        (print-row x y (first rows))
        (recur (+ y keyspace-h) (rest rows))))))

(defn print-layer [x y layer]
  (print-block x y (:left layer))
  (print-block (+ x hand-w outer-pad-w) y (:right layer))
  (print-row (+ x (* 3 keyspace-w)) (+ y (* 3 keyspace-h)) (get-in layer [:thumbs :left]))
  (print-row (+ x hand-w outer-pad-w) (+ y (* 3 keyspace-h)) (get-in layer [:thumbs :right])))

(defn print-board [x y keymap]
  (loop [x (+ x outer-pad-w)
         y (+ y outer-pad-h)
         layers keymap]
    (if (empty? layers)
      nil
      (do
        (print-layer x y (first layers))
        (recur x (+ y outer-pad-h layer-h) (rest layers))))))

(defn -main
  [& args]
  (println (format "<svg width=\"%s\" height=\"%s\" viewBox=\"0 0 %s %s\" xmlns=\"http://www.w3.org/2000/svg\">"
                   board-w board-h board-w board-h))
  (println (format "<style>
%s
</style>" style))
  (print-board 0 0 keymap)
  (println "</svg>"))
