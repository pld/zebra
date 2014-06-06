(ns ona.viewer.views.home
  (:use [ona.utils.string :only [substring?]])
  (:require [ona.api.user :as api]
            [ona.api.organization :as api-orgs]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.home :as home]))

(defn- get-public-private-dataset-counts
  [all-datasets]
  (let [freq (frequencies (for [dataset all-datasets]
                            (:public_data dataset)))]
    {:no-of-public (get freq true)
     :no-of-private (get freq false)}))

(defn- search-datasets
  "Return datasets' with a dataset title matching the query."
  [query datasets]
  (remove
   nil?
   (for [dataset datasets]
     (if (substring? query (:title dataset))
       dataset))))

(defn dashboard
  "Render the users signed in home page."
  ([account]
     (dashboard account nil))
  ([account query]
     (let [username (:username account)
           all-datasets (datasets/all account)
           dataset-details (get-public-private-dataset-counts all-datasets)
           datasets (if query
                      (search-datasets query all-datasets)
                      all-datasets)
           orgs (api-orgs/all account)]
       (base/base-template
        "/"
        account
        "Home"
        (home/home-content username
                           datasets
                           dataset-details
                           query
                           orgs)
        nil
        orgs))))

(defn home-page
  "Render the signed out home page."
  ([session]
   (home-page session nil))
  ([session search-term]
    (if-let [account (:account session)]
      (dashboard account search-term)
      (accounts/login))))
