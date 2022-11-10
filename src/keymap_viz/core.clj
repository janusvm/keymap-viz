(ns keymap-viz.core
  (:require
   [hiccup.core :as h]
   [keymap-viz.config :as conf]
   [keymap-viz.rendering :as r]
   [mount.core :as m]
   [keymap-viz.utils :as u])
  (:gen-class))

(defn -main
  [& args]

  ;; Mount config state
  (m/start)

  (->> (r/render-keymap conf/keymap conf/layout)
       (apply r/render-svg (u/calc-keymap-size conf/keymap conf/layout))
       (h/html)
       (println)))
