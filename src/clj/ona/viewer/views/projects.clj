(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ona.viewer.templates.forms :only [new-project-form]]
        [ona.viewer.templates.projects :only [project-forms project-settings]]
        [ring.util.response :only [redirect-after-post]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.project :as api]))

(defn all
  "List all of the users projects."
  [account]
  (let [projects (api/all account)]
    (dashboard-items
      "Projects"
      (:username account)
      "/projects"
      (for [project projects]
        {:name (str project)}))))

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

(defn forms
  "Show the forms for a project."
  [account id]
  (let [project (api/get-project account id)]
    (base-template
     (str "/project/" (:id project) "/forms")
     account
     "Project Forms"
     (project-forms project))))

(defn settings
  "Show the settings for a project."
  [account id]
  (let [project (api/get-project account id)
        username (:username account)
        ;; TODO fille this with the shared users when API finished
        shared-user [username]]
    (base-template
     (str "/project/" (:id project) "/settings")
     account
     "Project Settings"
     (project-settings project username shared-user))))


(defn create
  "Create a new project for the current user."
  [account params]
  (let [owner (make-url (str "users/" (:username account)))
        data {:name (:name params)
              :owner owner}]
    (try+
     (let [project (api/create account data)]
       (redirect-after-post (str "/project/" (:id project) "/settings")))
     (catch vector? errors
       (new-project account errors)))))
