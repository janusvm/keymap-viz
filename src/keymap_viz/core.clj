(ns keymap-viz.core
  (:require
   [hiccup.core :as h]
   [keymap-viz.config :as conf]
   [keymap-viz.rendering :as r]
   [mount.core :as m])
  (:gen-class))

(defn -main
  [& args]

  ;; Mount config state
  (m/start)

  (->> (r/render-keymap conf/keymap conf/layout)
       (apply r/render-svg)
       (h/html)
       (println)))
