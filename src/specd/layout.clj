(ns specd.layout
  (:require [clojure.string :refer [blank?]]
            [hiccup.page :refer [html5 include-css include-js]]
            [specd.db :as db]))

;; Main base.html
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

;; Helpers

(defn boot--form-group
  ""
  ([name type label]
   (boot--form-group name type label ""))
  ([name type label placeholder]
   [:div.form-group
    [:label.control-label.col-sm-3 {:for name} label ":"]
    [:div.col-sm-9
     [:input.form-control {:name name :type type :placeholder placeholder}]]]))

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
          [:td (when (not (blank? type)) (get db/route-types (Integer. type)))]
          [:td elevation]]))]])

(defn boot--route-type-select
  ""
  [name label]
  [:div.form-group
   [:label.control-label.col-sm-3 {:for name} label ":"]
   [:div.col-sm-9
    [:select.form-control {:name name}
     (for [[id name] db/route-types]
       [:option {:value id} name])]]])

(defn boot--route-entity-select
  ""
  [name label]
  [:div.form-group
   [:label.control-label.col-sm-3 {:for name} label ":"]
   [:div.col-sm-9
    [:select.form-control {:name name}
     (for [{id :id name :name} (db/list-routes {})]
       [:option {:value id} name])]]])


(defn boot--btn-panel
  ""
  []
  [:div.btn-group
   [:a.btn.btn-default {:href "/new"} "Add new"]
   [:a.btn.btn-default {:href "/find"} "Find route"]
   [:a.btn.btn-default {:href "/combine"} "Combine routes"]])

(defn submit
  ""
  []
  [:div.form-group
   [:div.col-sm-offset-3.col-sm-3
    [:input.btn.btn-default {:type "submit"}]]])

;; Forms

(defn route-new-form
  "Routes added here"
  []
  [:div.row
   [:div.col-xs-8.col-md-8
    [:form.form-horizontal {:action "/new" :method "POST"}
     [:h2 "Add new route"]
     (boot--form-group "name" "text" "Name" "Name")
     (boot--route-type-select "type" "Route type")
     (boot--form-group "checkpoints" "text" "Checkpoints" "Command separated list of checkpoints: 1,3,6")
     (boot--form-group "length" "number" "Route length" "In feets")
     (boot--form-group "level" "text" "Skill level" "level")
     (boot--form-group "elevation" "number" "Elevation" "In feets")
     (submit)]]])

(defn find-form
  "Find routes form here"
  []
  [:div.row
   [:div.col-xs-8.col-md-8
    [:h2 "Find route"]
    [:form.form-horizontal {:action "/" :method "GET"}
     (boot--route-type-select "type" "Route type")
     (boot--form-group "level" "text" "Skill level" "Level")
     (boot--form-group "elevation" "number" "Elevation" "Elevation in feets.")
     [:div.form-group
      [:label.control-label.col-sm-3 {:for "length-from"} "Length between" ":"]
      [:div.col-sm-9
       [:div.row
        [:div.col-sm-4
         [:input.form-control {:name "length-from" :type type :placeholder "from"}]]
        [:label.control-label.col-sm-1 "and"]
        [:div.col-sm-4
         [:input.form-control {:name "length-to" :type type :placeholder "to"}]]]]]
     (submit)]]])

(defn login-form
  ""
  []
  [:div.row
   [:div.col-xs-6.col-md6
    [:h2.col-sm-offset "Login"]
    [:form#login.form-horizontal {:action "/login" :method "POST"}
     (boot--form-group "email" "email" "Email Address" "Email")
     (boot--form-group "pasw" "password" "Password" "Password")
     (submit)]]])

(defn signup-form
  ""
  []
  [:div.row
   [:div.col-xs-6.col-md6
    [:h2 "Register"]
    [:form#signup.form-horizontal {:action "/signup" :method "POST"}
     (boot--form-group "email" "email" "Email Address" "Email")
     (boot--form-group "pasw" "password" "Password" "Password")
     (boot--form-group "confirm_pasw" "password" "Confirm password" "Password")
     (submit)]]])

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
   (boot--btn-panel)
   (route-new-form)])

(defn find-route
  ""
  [{:keys [username]}]
  [:div
   (user-navbar username)
   [:div.row (boot--btn-panel) (find-form)]])

(defn combine-route
  ""
  [{:keys [username]}]
  [:div
   (user-navbar username)
   (boot--btn-panel)
   [:div.row
    [:div.col-xs-6.col-md6
     [:h2 "Combine routes"]
     [:form.form-horizontal {:action "/combine" :method "POST"}
      (boot--form-group "name" "text" "Name" "Name")
      (boot--route-entity-select "route1" "Route1")
      (boot--route-entity-select "route2" "Route2")
      (submit)]]]])

;; Some shortcut functions

(defn render-page
  "Helper for call layout functions."
  ([request page-title page-func]
   (render-page request page-title page-func {}))

  ([request page-title page-func context]
   (let [username (get-in request [:session :identity])]
     (application
      page-title (page-func (merge {:username username} context))))))
