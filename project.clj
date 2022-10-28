(defproject keymap-viz "0.1.0-SNAPSHOT"
  :description "Keymap visualisation tool"
  :url "https://github.com/janusvm/keymap-viz"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit"
            :comment "MIT License"
            :year 2022
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot keymap-viz.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
