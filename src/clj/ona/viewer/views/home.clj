(ns ona.viewer.views.home
  (:use [hiccup core page]
        [ona.viewer.views.datasets :only [datasets]]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]))

(defn sign-in []
  (base
   [:h1 "Sign in"]
   [:form {:action "/signin" :method "post"}
    [:input {:type "text" :name "username"}]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "Sign in"}]]))

(defn dashboard [account]
  (base
   [:h1 "Welcome back " (:username account)]
   (datasets account)))

(defn submit-sign-in [params]
  (let [{:keys [username password]} params
        account {:username username :password password}
        profile (api/profile account)]
    (if-not (:detail profile)
      (assoc
          (response/redirect "/")
        :session {:account account})
      (sign-in))))

(defn home-page [session]
  (if-let [account (:account session)]
    (dashboard account)
    (sign-in)))

(defn sign-out []
  {:body (base
          [:h1 "Successfully logged out."])
   :session nil})
