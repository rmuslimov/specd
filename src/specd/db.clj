(ns specd.db
  (:require [environ.core :refer [env]]
            [heroku-database-url-to-jdbc.core :refer [korma-connection-map]]
            [korma
             [core :refer :all]
             [db :refer [defdb]]]
            [specd.utils :refer [pbkdf2]]))

(defdb db (korma-connection-map (env :database-url)))

(defentity users)

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

;; (add-user "admin@specd.com" "aaa")
;; (if-user-is-active "admin" "aaa")
;; (select users)
