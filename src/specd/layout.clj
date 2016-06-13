(ns specd.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn application [title & content]
  (html5
   {:lang "en"}
   [:head
    [:title title]
    (include-css "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.5/css/bootstrap.css")
    (include-js "out/goog/base.js")
    (include-js "main.js")
    [:body
     [:div.container content]]]))

(defn home []
  [:div.row
   [:form {:action "/logout" :method "POST"}
    [:button {:type "submit" :class "btn btn-link"} "Logout"]]])


(defn boot--form-group
  ""
  [name type label placeholder]
  [:div.form-group
   [:label {:for name} label ":"]
   [:input.form-control {:name name :type type :placeholder placeholder}]])

(defn login-form
  ""
  []
  [:div.row
   [:div.col-xs-6.col-md6
    [:form.login {:action "/login" :method "POST"}
     [:h2 "Login"]
     (boot--form-group "email" "email" "Email Address" "Email")
     (boot--form-group "pasw" "password" "Password" "Password")
     [:input.btn.btn-default {:type "submit"}]]]])

(defn signup-form
  ""
  []
  [:div.row
   [:div.col-xs-6.col-md6
    [:form.signup {:action "/signup" :method "POST"}
     [:h2 "Login"]
     (boot--form-group "email" "email" "Email Address" "Email")
     (boot--form-group "pasw" "password" "Password" "Password")
     (boot--form-group "confirm_pasw" "password" "Confirm password" "Password")
     [:input.btn.btn-default {:type "submit"}]]]])

(defn login
  ""
  []
  (list (login-form) (signup-form)))
