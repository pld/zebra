(ns ona.viewer.views.profile
  (:use [hiccup core page]
        [ona.viewer.templates.base :only [base-template]]
        [ona.viewer.templates.forms :only [sign-up-form]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]))

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
