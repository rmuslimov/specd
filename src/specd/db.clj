(ns specd.db
  (:require [clojure
             [string :refer [blank?]]
             [walk :refer [keywordize-keys]]]
            [environ.core :refer [env]]
            [heroku-database-url-to-jdbc.core :refer [korma-connection-map]]
            [korma
             [core :refer :all]
             [db :refer [defdb]]]
            [specd.utils :refer [pbkdf2]]))

(defdb db (korma-connection-map (env :database-url)))

(defentity users)
(defentity routes)

(def route-types {1 "plain" 2 "mountain"})

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
  [{:strs [type level elevation length-from length-to]}]
  (let [routes (select* routes)]
    (select
     (as-> routes $
       (if (blank? type) $ (where $ {:type type}))
       (if (blank? level) $ (where $ {:level level}))
       (if (blank? elevation) $ (where $ {:elevation elevation}))
       (if (blank? length-from) $ (where $ {:length [> (Integer. length-from)]}))
       (if (blank? length-to) $ (where $ {:length [< (Integer. length-to)]}))
       ))))

(defn add-route
  "Filter out empty key/value pairs and insert to database."
  [{:strs [length elevation] :as record}]
  (let [filtered (filter #(not (blank? (second %))) record)
        casted {:length (Double. length) :elevation (Double. elevation)}]
    (insert routes
            (values (merge (keywordize-keys (into {} filtered)) casted)))))


;; (add-user "admin@admin.com" "admin")
;; (insert routes (values {:name "example route"}))
;; (insert routes (values {:name "example 3" :length 3}))
;; (if-user-is-active "admin" "aaa")
;; (select users) (select routes)
;; (delete routes)
