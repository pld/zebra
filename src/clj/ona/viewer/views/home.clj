(ns ona.viewer.views.home
  (:use [hiccup core page]
        [ona.viewer.views.datasets :only [datasets]]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]))

(defn sign-in
  "Render the signin page."
  []
  (base
   [:h1 "Sign in"]
   [:form {:action "/signin" :method "post"}
    [:input {:type "text" :name "username"}]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "Sign in"}]]))

(defn dashboard
  "Render the users signed in home page."
  [account]
  (base
   [:h1 "Welcome back " (:username account)]
   (datasets account)))

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
  {:body (base
          [:h1 "Successfully logged out."])
   :session nil})
