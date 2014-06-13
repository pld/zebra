(ns ona.viewer.views.profiles
  (:use [ona.viewer.templates.base :only [base-template]]
        [ona.viewer.templates.forms :only [sign-up-form]])
  (:require [ona.api.user :as api]
            [ona.api.dataset :as api-dataset]
            [ring.util.response :as response]
            [ona.viewer.templates.profiles :as profiles]
            [ona.viewer.urls :as u]))

(defn sign-up []
  (base-template "/join" "" "Register" (sign-up-form)))

(defn submit-sign-up
  "Create a new user from submitted profile data."
  [params]
  (let [{:keys [name username email password password2]} params
        profile (api/create params)]
    (base-template
             "/join"
             ""
             "Register"
             (str "Created a profile:" profile))))

(defn user-profile
  "Show profile for a username."
  [account username]
  (let [profile (api/profile account username)
        datasets (api-dataset/all account)]
    (base-template
     (u/profile username)
     account
     (:name profile)
     (profiles/user-profile profile datasets))))
