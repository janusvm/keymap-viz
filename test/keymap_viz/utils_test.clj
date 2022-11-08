(ns keymap-viz.utils-test
  (:require
   [test-utils :as tu]
   [clojure.test :refer :all]
   [keymap-viz.config :as conf]
   [keymap-viz.utils :as subject]
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

(deftest vector-sum-test
  (testing "Vector sum"
    (testing "of a single vector"
      (are [x] (= x (subject/vector-sum x))
        [1 2]
        [1 2 3]
        [1 2 3 4]))
    (testing "of two vectors"
      (is (= [2 5] (subject/vector-sum [1 2] [1 3]))))))

(deftest calc-key-corner-test
  (testing "Calculating key corner"
    (testing "of rect type key"
      (let [alpha-key {:type :rect :size 1 :pos [1 2]}
            lctl-key {:type :rect :size 1.25 :pos [0 4]}
            space-key {:type :rect :size 6.25 :pos [3.75 4]}]
        (is (= [120. 165.] (subject/calc-key-corner alpha-key)))
        (is (= [75. 275.] (subject/calc-key-corner lctl-key)))
        (is (= [600. 275.] (subject/calc-key-corner space-key)))))))

(deftest calc-board-size-test
  (testing "Calculating board size"
    (testing "of 3x3 macropad"
      (let [layout (tu/load-edn-file "3x3.edn")]
        (is (= [180. 165.] (subject/calc-board-size layout)))))))
