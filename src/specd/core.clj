(ns specd.core
  (:gen-class)
  (:require [com.stuartsierra.component :as cmp]
            [compojure
             [core :refer :all]
             [route :as route]]
            [environ.core :refer [env]]
            [prone.middleware :as prone]
            [ring.middleware
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]
             [session :refer [wrap-session]]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.util.response :refer [redirect]]
            [specd
             [db :refer [add-route add-user if-user-is-active
                         list-routes create-new-route-from]]
             [layout :as layout]
             [utils :refer [all-the-sessions]]]
            [system.components.jetty :refer [new-web-server]]))

;; ----------------------------------
;; Auth functions

(defn authorized?
  "To pass auth having :identity in session is enough."
  [request]
  (boolean (get-in request [:session :identity])))

(defn unauthorized-handler [request]
  "If user is unathorized, redirect him to root page"
  (let [next-url (or (get-in request [:query-params :next] "/") (:uri request))]
    (redirect (format "/login?next=%s" next-url))))

(defn wrap-with-auth
  "All protected routes check if request authorized? and if not, redirect to root page."
  [handler]
  (fn [request]
    (if-not (authorized? request)
      (unauthorized-handler request)
      (handler request))))

;; ----------------------------------
;; Controllers

(defn add-new-route
  "Process post request and added new route to database."
  [request]
  (let [params (:form-params request)]
    (add-route params))
  (redirect "/"))

(defn login-auth
  "Login user, set :identity and redirect to next url"
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
  "Add user to database, clear session and direct to root page."
  [request]
  (let [username (get-in request [:form-params "email"])
        password (get-in request [:form-params "pasw"])
        confirm-password (get-in request [:form-params "confirm_pasw"])]
    (if (= password confirm-password)
      (add-user username password)))
  ;; Redirect back to same page
  (-> (redirect "/") (assoc :session {})))

(defn- home
  "List of routes with applied filters page."
  [request]
  (let [params (:query-params request)
        routes (list-routes params)]
    (layout/render-page request "Home" layout/home {:records routes})))

(defn- combine-routes
  "Add new routes based on few others."
  [request]
  (let [params (:params request)
        route-name (:name params)
        route-ids (map read-string (vals (dissoc params :name)))]
    (apply create-new-route-from route-name route-ids))
  (redirect "/"))

;;
;; ----------------------------------
;; Routes

;; Routes with no access control
(defroutes public-routes
  (GET "/login" [] (layout/application "Login" (layout/login)))
  (POST "/login" [] login-auth)
  (POST "/logout" [] logout)
  (POST "/signup" [] register))

;; Routes and auth handler
(defroutes protected-routes
  (GET "/" [] home)
  (GET "/new" [] #(layout/render-page % "Add new route" layout/add-new-route))
  (POST "/new" [] add-new-route)
  (GET "/find" [] #(layout/render-page % "Find route" layout/find-route))
  (GET "/combine" [] #(layout/render-page % "Combine route" layout/combine-route))
  (POST "/combine" [] combine-routes))

;; Combine them here
(defroutes app-routes
  public-routes
  (wrap-routes protected-routes wrap-with-auth)
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

;; Basic wrappers for Ringx
(def app
  (as-> app-routes $
    (wrap-keyword-params $)
    (wrap-params $)
    (wrap-session $ {:store (memory-store all-the-sessions)})
    ;; This is cool prone wrapper, helps to debug
    (prone/wrap-exceptions $)))

(defn prod-system []
  (cmp/system-map
   :web (new-web-server (or (Integer. (env :port)) 5000) app)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (cmp/start (prod-system)))
