(ns ona.viewer.views.home
  (:use [ona.utils.seq :only [select-value]]
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
                                      (:name %))
                                  projects))]
    project
    (api-project/create account
                        (default-project-info account))))

(defn- orphan-datasets
  [account projects]
  (let [datasets (api-dataset/all account)
        project-ids (map #(-> % :url s/last-url-param) projects)
        project-datasets (flatten
                          (map #(api-project/get-forms account %) project-ids))]
    (clojure.set/difference (set datasets) (set project-datasets))))

(defn- move-datasets-to-user-project
  "Create and move datasets to a default project if needed."
  [account]
  (let [projects (api-project/all account)
        project-id (-> (get-or-create-default-project account projects)
                       :url
                       s/last-url-param)
        datasets (orphan-datasets account projects)]
    (doall (map #(api-dataset/move-to-project account
                                              (:formid %)
                                              project-id) datasets))))

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
     ;; TODO run this in the background or on demand, future is a quick fix
     (move-datasets-to-user-project account)
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
