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
        freq (frequencies (for [dataset datasets]
                            (:public_data dataset)))
        dataset-details {:no-of-public (get freq true) :no-of-private (get freq false)}]
    (base/base-template
      "/"
      account
      "Home"
      (home/home-content
        username
        datasets
        dataset-details))))

(defn home-page
  "Render the signed out home page."
  [session]
  (if-let [account (:account session)]
    (dashboard account)
    (accounts/login)))
