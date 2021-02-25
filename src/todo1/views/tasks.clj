(ns todo1.views.tasks
  (:require [formative.core :as f]))

(defn task
  [{:keys [id name finished]}]
  [:li.list-group-item
   [:a {:href (str "/tasks/" id)}
    (if finished
      [:s name]
      name)]])

(defn tasks [task-list]
  [:div
   [:a.btn.btn-primary {:href "/create"} "Create new task"]
   (into [:ul.list-group] (map task task-list))])

(defn task-full
  [{:keys [id name finished description]}]
  [:div.card
   [:div.card-header name]
   [:div.card-body
    description
    [:div.row
     [:div.col
      [:form {:action (str "/tasks/" id "/"
                           (if finished "unfinish" "finish"))
              :method :post}
       [:input.btn.btn-primary {:type "submit"
                                :value (if finished "Mark unfinished" "Mark finished")}]]]
     [:div.col
      [:a.btn.btn-primary {:href (str "/tasks/" id "/edit")} "Edit"]]
     [:div.col
      [:form {:action (str "/tasks/" id "/delete")
              :method :post}
       [:input.btn.btn-danger {:type "submit" :value "Remove"}]]]]]])

(def task-empty-edit-form
  {:fields [{:name :name}
            {:name :description :type :textarea}
            {:name :finished :type :checkbox}]})

(defn task-edit-form [{:keys [name finished description]}]
  (merge task-empty-edit-form
         {:values {:name name
                   :description description
                   :finished finished}}))

(defn task-edit
  [{:keys [id name finished description] :as task}]
   [:div.card
    [:div.card-header (str "Edit task: " name)]
    [:div.card-body
     (f/render-form (task-edit-form task))]])

(defn task-create []
   [:div.card
    [:div.card-header (str "Create task")]
    [:div.card-body
     (f/render-form task-empty-edit-form)]])
