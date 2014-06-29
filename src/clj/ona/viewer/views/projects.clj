(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [profile-with-projects]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ring.util.response :only [redirect-after-post]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.organization :as api-org]
            [ona.api.project :as api]
            [ona.api.user :as api-user]
            [ona.api.dataset :as api-dataset]
            [ona.viewer.templates.projects :as template]
            [ona.viewer.urls :as u]
            [ona.utils.time :as t]
            [ona.viewer.helpers.projects :as h]))

(defn owners
  "Return possible project owners for account."
  [account]
  (cons (:username account)
        (map #(:org %) (api-org/all account))))

(defn new-project
  "Form for creating a new project."
  ([account owner]
     (new-project account owner nil))
  ([account owner errors]
     (base-template
      "/project"
      account
      "New Project"
      (template/new owner (owners account) errors))))

(defn show
  "Show the project."
  [account owner id]
  (let [project (api/get-project account id)
        forms (api/get-forms account id)
        profile (profile-with-projects account)
        latest-form (h/latest-submitted-form forms)
        all-submissions (h/all-submissions forms account)]
    (base-template
     (u/project-show id owner)
     account
     "Project Forms"
     (template/show owner project forms profile latest-form all-submissions))))

(defn settings
  "Show the settings for a project."
  [account owner id]
  (let [project (api/get-project account id)
        username (:username account)
        ;; TODO fill this with the shared users when API finished
        shared-user [username]]
    (base-template
     (u/project-settings project owner)
     account
     "Project Settings"
     (template/settings owner project (owners account) username shared-user))))


(defn create
  "Create a new project for the current user."
  [account params]
  (let [owner (:owner params)
        data {:name (:name params)}]
    (try+
     (let [project (api/create account data owner)]
       (redirect-after-post (u/project-settings owner project)))
     (catch vector? errors
       (new-project account owner errors)))))
