(ns ona.viewer.views.accounts
  (:use [ona.viewer.templates.base :only [base-template]])
  (:require [ona.api.user :as api]
            [ona.viewer.templates.accounts :as template]
            [ring.util.response :as response]))

(defn login
  "Render the login page."
  []
  (base-template "/" "" "Login" (template/login-form)))

(defn submit-login
  "Process submitted login details and log the user in."
  [params]
  (let [{:keys [username password]} params
        account {:username username :password password}
        profile (api/profile account username)]
    (if-not (:detail profile)
      (assoc
        (response/redirect "/")
        :session {:account account})
      (login))))

(defn logout
  "Logout the user by empying the session."
  []
  {:status 302
   :headers {"Location" "/"}
   :body ""
   :session nil})
