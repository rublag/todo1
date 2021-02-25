(ns todo1.controllers.tasks
  (:require [todo1.views.tasks :as view]
            [todo1.views.layout :as layout]
            [todo1.models.tasks :as model]))

(defn task-list [req]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body (let [tasks (model/get-tasks-short model/pg)]
           (println (view/tasks tasks))
           (layout/render (view/tasks tasks)))})
