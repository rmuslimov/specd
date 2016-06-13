(ns specd.db
  (:require [environ.core :refer [env]]
            [heroku-database-url-to-jdbc.core :refer [korma-connection-map]]
            [korma
             [core :refer :all]
             [db :refer [defdb]]]
            [specd.utils :refer [pbkdf2]]))

(defdb db (korma-connection-map (env :database-url)))

(defentity users)
(defentity routes)

(defn add-user
  "Add user to database."
  [username password]
  (let [enc-password (pbkdf2 password (env :password-salt))]
    (insert users (values {:email username :password enc-password}))))

(defn if-user-is-active
  "Find user and check if password matches."
  [username password]
  (let [matching-pasw (-> (select users (where {:email username})) first :password)]
    (and matching-pasw (= (pbkdf2 password (env :password-salt)) matching-pasw))))

(defn list-routes
  "Records from routes database"
  []
  (select routes))

(defn add-route
  ""
  [record]
  (let [filtered (filter #(not (empty? (second %))) record)]
    (insert routes
            (values (into {} filtered)))))


;; (add-user "admin@admin.com" "admin")
;; (insert routes (values {:name "example route"}))
;; (insert routes (values {:name "example 3" :length 3}))
;; (if-user-is-active "admin" "aaa")
;; (select users) (select routes)
