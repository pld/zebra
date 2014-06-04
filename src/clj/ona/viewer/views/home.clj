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
        public-datasets (count (filter true? (for [dataset datasets] 
                                               (:public_data dataset))))
        private-datasets (count (filter false? (for [dataset datasets]
                                                (:public_data dataset))))
        dataset-details {:no-of-public public-datasets :no-of-private private-datasets}]
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
