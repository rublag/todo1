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

(defn handler2 [req method
                {:keys [who] :as params}]
  (println req)
  (println params)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "Hello, " who "!")})

(defn index [& _]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (index-view)})

(def app
  (route handler
         ["/" [:get] index]
         ["/:who" [:get] handler2]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-server app {:port 8080}))
