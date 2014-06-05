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
  ([account]
   (dashboard account nil))
  ([account search-term]
  (let [username (:username account)
        all-datasets (datasets/all account)
        freq (frequencies (for [dataset all-datasets]
                            (:public_data dataset)))
        dataset-details {:no-of-public (get freq true) :no-of-private (get freq false)}
        filtered  (if search-term
                    (remove
                    nil?
                    (for [dataset all-datasets]
                      (if-not (zero? (count (filter #(= % search-term) (vals dataset))))
                      dataset
                      nil))))
        datasets (if search-term
                   filtered
                   all-datasets)]
    (base/base-template
      "/"
      account
      "Home"
      (home/home-content
        username
        datasets
        dataset-details)))))

(defn home-page
  "Render the signed out home page."
  ([session]
   (home-page session nil))
  ([session search-term]
    (if-let [account (:account session)]
      (dashboard account search-term)
      (accounts/login))))
