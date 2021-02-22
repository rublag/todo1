(ns todo1.views.index
  (:use [hiccup.page :as page]))

(defn index []
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    (page/include-css "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css")]
   [:body
    [:h1 "Hello, World!"]]))
