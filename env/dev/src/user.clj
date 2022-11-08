(ns user
  "Userspace functions for local development."
  (:require [mount.core :as m]))

(defn start []
  (m/start))

(defn stop []
  (m/stop))

(defn restart []
  (stop)
  (start))
