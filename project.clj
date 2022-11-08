(defproject keymap-viz "0.1.0-SNAPSHOT"
  :description "Keymap visualisation tool"
  :url "https://github.com/janusvm/keymap-viz"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit"
            :comment "MIT License"
            :year 2022
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [mount "0.1.16"]
                 [hiccup "1.0.5"]]

  :source-paths ["src"]
  :resource-paths ["resources"]
  :test-paths ["test"]

  :main ^:skip-aot keymap-viz.core
  :target-path "target/%s/"

  :profiles
  {:uberjar {:aot :all
             :omit-source true
             :uberjar-name "keymap-viz.jar"}

   :dev [:project/dev]

   :project/dev {:source-paths ["env/dev/src"
                                "env/test/src"]
                 :resource-paths ["env/dev/resources"
                                  "env/test/resources"]
                 :repl-options {:init-ns user}}})
