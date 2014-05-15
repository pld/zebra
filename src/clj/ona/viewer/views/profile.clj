(ns ona.viewer.views.profile
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]))

(defn sign-up []
  (base
   [:h1 "Register"]
   [:form {:action "/join" :method "post"}
    [:input {:type "text" :name "name" :placeholder "Name"}]
    [:input {:type "text" :name "username" :placeholder "Username" :required "true"}]
    [:input {:type "text" :name "email" :placeholder "Email" :required "true"}]
    [:input {:type "password" :name "password" :placeholder "Password" :required "true"}]
    [:input {:type "password" :name "password2" :placeholder "Password(again)" :required "true"}]
    [:input {:type "submit" :value "Sign up"}]]))

(defn submit-sign-up
  "Create a new user from submitted profile data."
  [params]
  (let [{:keys [name username email password password2]} params
        profile (api/create params)]
    {:body (base
            [:h1 "Created a profile"]
            [:p (str profile)])}))
