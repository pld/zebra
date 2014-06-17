(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ona.viewer.templates.forms :only [new-project-form]]
        [ona.viewer.templates.projects :only [project-show project-settings]]
        [ring.util.response :only [redirect-after-post]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.project :as api]
            [ona.api.user :as api-user]
            [ona.api.dataset :as api-dataset]
            [ona.viewer.urls :as u]
            [ona.utils.time :as t]
            [ona.viewer.helpers.projects :as h]))

(defn all
  "List all of the users projects."
  [account owner]
  (let [projects (api/all account owner)]
    (dashboard-items
      "Projects"
      account
      "/projects"
      (for [project projects]
        {:name (str project)}))))

(defn new-project
  "Form for creating a new project."
  ([account owner]
     (new-project account owner nil))
  ([account owner errors]
      (base-template
       "/project"
       account
       "New Project"
       (new-project-form owner errors))))

(defn show
  "Show the project."
  [account owner id]
  (let [project (api/get-project account owner id)
        forms (api/get-forms account owner id)
        profile (api-user/profile account)
        latest-form (h/latest-submitted-form forms)
        all-submissions (h/all-submissions forms account)]
    (base-template
     (u/project-show id owner)
     account
     "Project Forms"
     (project-show owner project forms profile latest-form all-submissions))))

(defn settings
  "Show the settings for a project."
  [account owner id]
  (let [project (api/get-project account owner id)
        username (:username account)
        ;; TODO fille this with the shared users when API finished
        shared-user [username]]
    (base-template
     (u/project-settings project owner)
     account
     "Project Settings"
     (project-settings owner project username shared-user))))


(defn create
  "Create a new project for the current user."
  [account params]
  (let [owner (:owner params)
        data {:name (:name params)}]
    (try+
     (let [project (api/create account data owner)]
       (redirect-after-post (u/project-settings project owner)))
     (catch vector? errors
       (new-project account errors)))))
