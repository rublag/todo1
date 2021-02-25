(ns todo1.controllers.tasks
  (:require [formative.parse :as fp]
            [ring.middleware.params :as params]
            [ring.middleware.nested-params :as nested-params]
            [ring.util.response :as response]
            [todo1.views.tasks :as view]
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

(defn task-edit-get [req]
  (if-let [id (str->int (:id (:uri-params req)))]
    (if-let [task (model/get-task model/pg id)]
      {:status 200
       :headers {"Content-type" "text/html"}
       :body (layout/render (view/task-edit task))})))

(defn task-edit-post* [req]
  (if-let [id (str->int (:id (:uri-params req)))]
    (let [params (:params req)]
      (if-let [task (fp/parse-params view/task-empty-edit-form params)]
        (do (model/update-task model/pg task id)
            (-> (str "/tasks/" id)
                (response/redirect :see-other)
                (response/content-type "text/html")))))))

(def task-edit-post
  (-> task-edit-post*
      params/wrap-params
      nested-params/wrap-nested-params))

(defn task-create-get [req]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body (layout/render (view/task-create))})

(defn task-create-post* [req]
  (let [params (:params req)]
    (if-let [task (fp/parse-params view/task-empty-edit-form
                                                                params)]
      (let [id (model/create-task model/pg task)]
        (println id)
        (-> (str "/tasks/" id)
            (response/redirect :see-other)
            (response/content-type "text/html"))))))

(def task-create-post
  (-> task-create-post*
      params/wrap-params
      nested-params/wrap-nested-params))

(defn task-remove [req]
  (println "test!!!!!!!!!!!!!!!!!!!")
  (when-let [id (str->int (:id (:uri-params req)))]
    (model/delete-task model/pg id)
    (-> "/"
        (response/redirect :see-other)
        (response/content-type "text/html"))))
