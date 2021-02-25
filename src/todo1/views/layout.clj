(ns todo1.views.layout
  (:require [hiccup.page :as page]))

(defn render [html]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    (page/include-css "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css")]
   [:body
    html]))
