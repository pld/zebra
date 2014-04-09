(ns ona.viewer.views.home
  (:use [hiccup core page]
        [ona.viewer.views.partials :only (base)])
  (:require [ona.viewer.api :as api]))

(defn sign-in []
  (base
   [:h1 "Sign in"]
   [:form {:action "/signin" :method "post"}
    [:input {:type "text" :name "username"}]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "Sign in"}]]))

(defn dashboard [account]
  (base
   [:h1 "Welcome back " (:username account)]))

(defn submit-sign-in [params]
  (let [{:keys [username password]} params
        account {:username username :password password}
        profile (api/user-profile account)]
    {:body (base
            [:h1 "Signed in as " username]
            [:p (str profile)])
     :session {:account account}}))

(defn home-page [session]
  (if-let [account (:account session)]
    (dashboard account)
    (sign-in)))

(defn sign-out []
  {:body (base
          [:h1 "Successfully logged out."])
   :session nil})
