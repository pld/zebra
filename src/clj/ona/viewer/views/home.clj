(ns ona.viewer.views.home
  (:require [ona.api.user :as api]
            [ona.api.organization :as api-orgs]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.home :as home]))

(defn dashboard
  "Render the users signed in home page."
  [account]
  (let [username (:username account)
        datasets (datasets/all account)
        orgs (api-orgs/all account)]
    (base/base-template
      "/"
      (:username account)
      "Home"
      (home/home-content
        username
        datasets)
      orgs)))

(defn home-page
  "Render the signed out home page."
  [session]
  (if-let [account (:account session)]
    (dashboard account)
    (accounts/login)))
