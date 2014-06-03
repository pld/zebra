(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ona.viewer.templates.forms :only [new-project-form]]
        [ona.viewer.templates.projects :only [project-settings]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.project :as api]))

(defn all
  "List all of the users projects."
  [account]
  (let [projects (api/all account)]
    (dashboard-items
      "Projects"
      (:username account)
      "projects/"
      (for [project projects]
        {:item-name (str project)}))))

(defn new-project
  "Form for creating a new project."
  ([account]
     (new-project account nil))
  ([account errors]
      (base-template
       "/project"
       account
       "New Project"
       (new-project-form errors))))

(defn settings
  "Show the settings for a project."
  [account project]
  (base-template
   (str "/project/" (:id project) "settings")
   account
   "Project Settings"
   (project-settings (:name project) [(:username account)])))

(defn create
  "Create a new project for the current user."
  [account params]
  (let [owner (make-url (str "users/" (:username account)))
        data {:name (:name params)
              :owner owner}]
    (try+
     (settings account (api/create account data))
     (catch vector? errors
       (new-project account errors)))))
