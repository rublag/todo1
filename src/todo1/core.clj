(ns todo1.core
  (:use org.httpkit.server)
  (:use todo1.router)
  (:use [todo1.views.index :only [index] :rename {index index-view}])
  (:gen-class))

(defn handler [req method route-params]
  (println req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "hello HTTP!"})

(defn handler2 [req]
  (println req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "Hello, " (get-in req [:uri-params :who]) "!")})

(defn index [& _]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (index-view)})

(def app
  (route handler
         ["/pub/*" [:get] (route-static "public" index)]
         ["" [:get] index]
         ["/:who" [:get] handler2]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Clojure loaded.")
  (run-server app {:port 8080}))
