(ns ona.viewer.views.home
  (:use [hiccup core page] [ona.viewer.views.partials :only [base]]
        [ona.viewer.views.templates :only [base-template
                                           dashboard-items
                                           sign-in-form]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]
            [ona.viewer.views.datasets :as datasets]))

(defn sign-in
  "Render the sign in page."
  []
  (base-template "/" "" "Sign-in" (sign-in-form)))

(defn dashboard
  "Render the users signed in home page."
  [account]
  (dashboard-items "Datasets"
                   (:username account)
                   "dataset/"
                   (datasets/all account)
                   nil))

(defn submit-sign-in
  "Process submitted sign in details and log the user in."
  [params]
  (let [{:keys [username password]} params
        account {:username username :password password}
        profile (api/profile account)]
    (if-not (:detail profile)
      (assoc
        (response/redirect "/")
        :session {:account account})
      (sign-in))))

(defn home-page
  "Render the signed out home page."
  [session]
  (if-let [account (:account session)]
    (dashboard account)
    (sign-in)))

(defn sign-out
  "Sign out the user by empying the session."
  []
  {:body (base-template "signout" "" "Sign Out" "Successfully logged out.")
   :session nil})
