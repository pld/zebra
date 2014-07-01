(ns ona.viewer.views.home
  (:use [ona.utils.seq :only [diff select-value]]
        [ona.utils.string :only [substring?]]
        [ona.viewer.helpers.projects :only [project-details]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.dataset :as api-dataset]
            [ona.api.organization :as api-orgs]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]
            [ona.utils.string :as s]
            [ring.util.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.home :as home]))

(defn- default-project-info
  [account]
  (let [username (:username account)
        name-prefix (select-value account [:name :username])]
    {;; TODO check that short-name is when API supports this
     :short-name (str username "-project")
     :name (str name-prefix "'s Project")}))

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

(defn- get-or-create-default-project
  [account projects]
  (if-let [project (first (filter #(= (:name (default-project-info account))
                                      (-> % :project :name))
                                  projects))]
    project
    (api-project/create account
                        (default-project-info account))))

(defn- orphan-datasets
  [account projects]
  (let [datasets (api-dataset/all account)
        project-datasets (flatten
                          (map #(:forms %) projects))]
    (diff datasets project-datasets)))

(defn- move-datasets-to-user-project
  "Create and move datasets to a default project if needed."
  [account projects]
  (let [project-id (-> (get-or-create-default-project account projects)
                       :url
                       s/last-url-param)
        datasets (orphan-datasets account projects)]
    (doall (map #(api-dataset/move-to-project account
                                              (:formid %)
                                              project-id) datasets))))

(defn- search-collection
  "Return collections with a key matching the query."
  [query collection k sub-k]
  (remove
   nil?
   (for [member collection]
     (if (substring? query (-> member sub-k k))
       member))))

(defn dashboard
  "Render the users signed in home page."
  ([account]
     (dashboard account nil))
  ([account query]
     (let [username (:username account)
           projects (project-details account username)
           ;; if we moved any, fetch projects again
           projects (if (empty? (move-datasets-to-user-project account
                                                               projects))
                      projects
                      (project-details account username))
           project-details (get-public-private-project-counts projects)
           projects (if query
                      (search-collection query projects :name :project)
                      projects)
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
  ([account]
   (home-page account nil))
  ([account search-term]
    (if account
      (dashboard account search-term)
      (accounts/login))))
