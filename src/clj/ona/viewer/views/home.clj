(ns ona.viewer.views.home
  (:use [ona.utils.string :only [substring?]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.user :as api]
            [ona.api.organization :as api-orgs]
            [ona.api.user :as api-user]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.home :as home]))

(defn- counts-for-collection
  [collection k]
  (let [freq (frequencies (map #(k %) collection))]
    {:num-public (get freq true 0)
     :num-private (get freq false 0)}))

(defn- get-public-private-dataset-counts
  [datasets]
  (counts-for-collection datasets :public_data))

(defn- get-public-private-project-counts
  [projects]
  ;; TODO should work with more project metadata
  ;; verify works when onadata#319 is closed.
  (counts-for-collection projects :public))

(defn- search-collection
  "Return collections with a key matching the query."
  [query collection k]
  (remove
   nil?
   (for [member collection]
     (if (substring? query (k member))
       member))))

(defn dashboard
  "Render the users signed in home page."
  ([account]
     (dashboard account nil))
  ([account query]
     (let [username (:username account)
           all-projects (project-details account username)
           project-details (get-public-private-project-counts all-projects)
           projects (if query
                      (search-collection query all-projects :name)
                      all-projects)
           orgs (api-orgs/all account)
           profile (api-user/profile account)]
       (base/base-template
        "/"
        account
        "Home"
        (home/home-content profile
                           projects
                           project-details
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
