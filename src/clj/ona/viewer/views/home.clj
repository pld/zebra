(ns ona.viewer.views.home
  (:use [ona.viewer.views.templates :only [dashboard-items]])
  (:require [ona.api.user :as api]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]))

(defn dashboard
  "Render the users signed in home page."
  [account]
  (dashboard-items "Datasets"
                   (:username account)
                   "dataset/"
                   (datasets/all account)
                   nil))

(defn home-page
  "Render the signed out home page."
  [session]
  (if-let [account (:account session)]
    (dashboard account)
    (accounts/login)))
