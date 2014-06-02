(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ona.viewer.templates.forms :only [new-project-form]])
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
        {:item-name (str project)})
      (new-project-form))))

(defn new
  "Form for creating a new project."
  [account]
  (base-template
   "/project"
   account
   "New Project"
   (new-project-form)))

(defn create
  "Create a new project for the current user."
  [account params]
  (let [owner (make-url (str "users/" (:username account)))
        data {:name (:name params)
              :owner owner}
        project (api/create account data)]
    (all account)))
