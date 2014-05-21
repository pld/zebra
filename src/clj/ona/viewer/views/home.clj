(ns ona.viewer.views.home
  (:require [ona.api.user :as api]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.home :as home]))

(defn dashboard
  "Render the users signed in home page."
  [account]
  (base/dashboard-items "Datasets"
                   (:username account)
                   "dataset/"
                   []
                   (home/home-content (datasets/all account) (:username account))))

(defn home-page
  "Render the signed out home page."
  [session]
  (if-let [account (:account session)]
    (dashboard account)
    (accounts/login)))
