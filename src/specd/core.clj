(ns specd.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure
             [core :refer :all]
             [route :as route]]))

(defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
