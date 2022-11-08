(ns test-utils
 (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defn load-edn-file [filename]
  (->> (io/resource filename)
       (slurp)
       (edn/read-string)
       (eval)))
