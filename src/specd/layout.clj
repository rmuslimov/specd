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

(defn boot--form-group
  ""
  ([name type label]
   (boot--form-group name type label ""))
  ([name type label placeholder]
   [:div.form-group
    [:label {:for name} label ":"]
    [:input.form-control {:name name :type type :placeholder placeholder}]]))

(defn boot--table
  ""
  [records]
  [:table.table
   [:thead
    [:tr
     [:th "#"]
     [:th "Name"]
     [:th "Length"]
     [:th "Skill level"]
     [:th "Type"]
     [:th "Elevation"]]]
   [:tbody
     (for [record records]
       (let [{:keys [id name length level type elevation]} record]
         [:tr
          [:th {:scope :row} id]
          [:td name]
          [:td length]
          [:td level]
          [:td type]
          [:td elevation]]))]])

(defn boot--btn-panel
  ""
  []
  [:div.btn-group
   [:a.btn.btn-default {:href "/new"} "Add new"]
   [:a.btn.btn-default {:href "/find"} "Find route"]
   [:a.btn.btn-default {:href "/combine"} "Combine routes"]])

;; Forms

(defn route-new-form
  "Routes added here"
  []
  [:div.row
   [:div.col-xs-6.col-md6
    [:form.login {:action "/new" :method "POST"}
     [:h2 "Add new route"]
     (boot--form-group "name" "text" "Name" "Name")
     (boot--form-group "type" "text" "Route type" "Route")
     (boot--form-group "checkpoints" "text" "Checkpoints")
     (boot--form-group "length" "number" "Route length")
     (boot--form-group "level" "text" "Skill level")
     (boot--form-group "elevation" "number" "Elevation")
     [:input.btn.btn-default {:type "submit"}]]]])

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

(defn user-navbar
  "Navigation bar."
  [username]
  [:div.row
   [:form {:action "/logout" :method "POST"}
    [:button.pull-right
     {:type "submit" :class "btn btn-link"} username ", " "Logout"]]])

;; Pages

(defn login
  ""
  []
  (list (login-form) (signup-form)))

(defn home [context]
  (let [{:keys [username records]} context]
    [:div
     (user-navbar username)
     [:div.row (boot--btn-panel) (boot--table records)]]))

(defn add-new-route [{:keys [username]}]
  [:div
   (user-navbar username)
   (route-new-form)])
