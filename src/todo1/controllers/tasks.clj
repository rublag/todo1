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

(defn str->int [s]
  (try (Integer/parseInt s)
       (catch NumberFormatException e nil)))

(defn task-full [req]
  (if-let [id (str->int (:id (:uri-params req)))]
    (if-let [task (model/get-task model/pg id)]
      {:status 200
       :headers {"Content-type" "text/html"}
       :body (layout/render (view/task-full task))})))

(defn task-edit [req]
  (if-let [id (str->int (:id (:uri-params req)))]
    (if-let [task (model/get-task model/pg id)]
      {:status 200
       :headers {"Content-type" "text/html"}
       :body (layout/render (view/task-edit task))})))
