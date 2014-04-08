(ns ona.viewer.views.home
  (:use [hiccup core page]
        [ona.viewer.views.partials :only (base)]))

(defn sign-in []
  (base
   [:h1 "Sign in"]
   [:form {:action "/signin" :method "post"}
    [:input {:type "text" :name "username"}]
    [:input {:type "password" :name "password"}]
    [:input {:type "submit" :value "Sign in"}]]))

(defn dashboard [account]
  (base
   [:h1 "Welcome back" (:username account)]))

(defn submit-sign-in [params]
  (let [account params]
    {:body (base
            [:h1 "Signed in as " (:username account)])
     :session {:account params}}))

(defn home-page [session]
  (if-let [account (:account session)]
    (dashboard account)
    (sign-in)))
