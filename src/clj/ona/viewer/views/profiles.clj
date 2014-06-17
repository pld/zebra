(ns ona.viewer.views.profiles
  (:use [ona.viewer.helpers.projects :only [project-details]]
        [ona.viewer.templates.base :only [base-template]]
        [ona.viewer.templates.forms :only [sign-up-form]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.user :as api]
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
  (try+
   (let [profile (api/profile account username)
         projects (project-details account username)]
     (base-template
      (u/profile username)
      account
      (:name profile)
      (profiles/user-profile profile projects)))
   (catch string? error
     ;; TODO return a proper not found page.
     error)))
