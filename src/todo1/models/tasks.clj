(ns todo1.models.tasks
  (:require [clojure.java.jdbc :as db]
            [honeysql.core :as sql]))

(def pg {:dbtype "postgresql"
         :dbname "clj_todo"
         :host "127.0.0.1"
         :user "clj"})

(defn get-tasks-short [db-spec]
  (let [sqlmap (sql/build :select [:id :name :finished]
                          :from :tasks)]
    (db/query pg (sql/format sqlmap))))

(defn get-tasks [db-spec]
  (let [sqlmap (sql/build :select :*
                          :from :tasks)]
    (db/query pg (sql/format sqlmap))))
