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
  [name type label placeholder]
  [:div.form-group
   [:label {:for name} label ":"]
   [:input.form-control {:name name :type type :placeholder placeholder}]])

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

(defn btn-panel
  ""
  []
  [:div.btn-group
   [:button.btn.btn-default "Add new"]
   [:button.btn.btn-default "Find route"]
   [:button.btn.btn-default "Combine routes"]])

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


;; Pages

(defn login
  ""
  []
  (list (login-form) (signup-form)))

(defn home [context]
  (let [{:keys [username records]} context]
    [:div
     [:div.row
      [:form {:action "/logout" :method "POST"}
       [:button.pull-right {:type "submit" :class "btn btn-link"} username ", " "Logout"]]]
     [:div.row (btn-panel) (boot--table records)]]))
