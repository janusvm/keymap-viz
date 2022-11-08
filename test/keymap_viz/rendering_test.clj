(ns keymap-viz.rendering-test
  (:require
   [clojure.test :refer :all]
   [keymap-viz.config :as conf]
   [keymap-viz.rendering :as subject]
   [mount.core :as m]))

(def ^:private test-config
  {:key-padding 3.
   :border-radius 5.
   :layer-margin 0.
   :grid-size-horz 60.
   :grid-size-vert 55.})

(defn- start-states [f]
  (m/start-with {#'conf/config test-config})
  (f)
  (m/stop))

(use-fixtures :once start-states)

(deftest make-key-test
  (testing "should merge options from keymap and layout"
    (testing "when key is given as a string"
      (is (= {:type :rect :size 1 :pos [6 2] :tap "H" :hold nil}
             (subject/make-key "H" {:type :rect :size 1 :pos [1 2]} [5 0]))))
    (testing "when key is given as a map"
      (is (= {:type :rect :size 1 :pos [3 1] :tap "T" :hold "Shift"}
             (subject/make-key {:tap "T" :hold "Shift"} {:type :rect :size 1 :pos [3 1]} [0 0]))))))

(deftest render-key-test
  (testing "should generate a Hiccup-ready data structure"
    (is (= [[:rect {:x 183. :y 58. :rx 5. :ry 5. :width 54. :height 49.}]
            [:text {:x 210. :y 82.5} "T"]]
           (subject/render-key {:type :rect :size 1 :pos [3 1] :tap "T" :hold "Shift"})))))
