(ns ona.viewer.views.accounts
  (:use [ona.viewer.views.templates :only [base-template
                                           login-form]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]))

(defn login
  "Render the login page."
  []
  (base-template "/" "" "Login" (login-form)))

(defn submit-login
  "Process submitted login details and log the user in."
  [params]
  (let [{:keys [username password]} params
        account {:username username :password password}
        profile (api/profile account)]
    (if-not (:detail profile)
      (assoc
        (response/redirect "/")
        :session {:account account})
      (login))))

(defn logout
  "Logout the user by empying the session."
  []
  {:body (base-template "logout" "" "Log Out" "Successfully logged out.")
   :session nil})
