(ns todo1.core
  (:require [org.httpkit.server :refer :all]
            [todo1.router :refer :all]
            [todo1.controllers.tasks :as tasks])
  (:gen-class))

(defn not-found [req]
  (println req)
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "Not found"})

(def app
  (route not-found
         ["/pub/*" [:get] (route-static "public" not-found)]
         ["" [:get] tasks/task-list]
         ["/tasks/:id" [:get] tasks/task-full]
         ["/tasks/:id/edit" [:get] tasks/task-edit-get]
         ["/tasks/:id/edit" [:post] tasks/task-edit-post]
         ["/create" [:get] tasks/task-create-get]
         ["/create" [:post] tasks/task-create-post]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Clojure loaded.")
  (run-server app {:port 8080}))
