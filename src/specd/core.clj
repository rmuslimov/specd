(ns specd.core
  (:gen-class)
  (:require [compojure
             [core :refer :all]
             [route :as route]]
            [ring.middleware
             [params :refer [wrap-params]]
             [session :refer [wrap-session]]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.util.response :refer [redirect]]
            [specd
             [db :refer [add-user if-user-is-active]]
             [layout :as layout]
             [utils :refer [all-the-sessions]]]))

;;
;; ----------------------------------
;; Controllers

(defn authorized?
  "To pass auth having :identity in session is enough."
  [request]
  (boolean (get-in request [:session :identity])))

(defn unauthorized-handler [request]
  "If user is unathorized, redirect him to root page"
  (let [next-url (or (get-in request [:query-params :next] "/") (:uri request))]
    (redirect (format "/login?next=%s" next-url))))

(defn home
  [request]
  (if-not (authorized? request)
    (unauthorized-handler request)
    (layout/application "Home" (layout/home))))

(defn login-auth
  "Login user"
  [request]
  (let [username (get-in request [:form-params "email"])
        password (get-in request [:form-params "pasw"])
        session (:session request)]
    (if (if-user-is-active username password)
      (let [next-url (get-in request [:query-params :next] "/")
            updated-session (assoc session :identity username)]
        (-> (redirect next-url)
            (assoc :session updated-session)))
      (unauthorized-handler request))))

(defn logout
  "Logout user. Clear session and redirect to root."
  [request]
  (-> (redirect "/") (assoc :session {})))

(defn register
  "Register user in database"
  [request]
  (let [username (get-in request [:form-params "email"])
        password (get-in request [:form-params "pasw"])
        confirm-password (get-in request [:form-params "confirm_pasw"])]
    (if (= password confirm-password)
      (add-user username password)))
  ;; Redirect back to same page
  (-> (redirect "/") (assoc :session {})))

;; ----------------------------------
;; Routes

(defroutes app-routes
  (GET "/" [] home)
  (GET "/login" [] (layout/application "Login" (layout/login)))
  (POST "/login" [] login-auth)
  (POST "/logout" [] logout)
  (POST "/signup" [] register)
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (as-> app-routes $
      (wrap-params $)
      (wrap-session $ {:store (memory-store all-the-sessions)})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
