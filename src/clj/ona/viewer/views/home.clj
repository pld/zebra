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
        no-public-datasets 0
        no-private-datasets 0
        public-datasets (loop [dataset datasets]
                             (when (:public_data dataset)
                               (recur (inc no-public-datasets))))
        private-datasets (loop [dataset datasets]
                          (when (not (:public_data dataset))
                            (recur (inc no-private-datasets))))
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
