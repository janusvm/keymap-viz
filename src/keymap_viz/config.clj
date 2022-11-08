(ns keymap-viz.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [mount.core :as m]))

(def ^:private default-config
  {:border-radius 6
   :grid-size-horz 60
   :grid-size-vert 50
   :key-padding 3
   :layer-margin 30
   :style :default})

(m/defstate default-style
  :start (->> (io/resource "styles/default.css")
              (slurp)))

;; TODO: make configurable instead of hardcoded
(m/defstate layout
  :start (->> (io/resource "layouts/split_3x5_2.edn")
              (slurp)
              (edn/read-string)
              (eval)))

;; TODO: make configurable instead of hardcoded
(m/defstate keymap
  :start (->>  (slurp "examples/keymaps/split_3x5_2.edn")
               (edn/read-string)
               (eval)))

(m/defstate config
  :start (merge default-config
                (:config keymap)))
