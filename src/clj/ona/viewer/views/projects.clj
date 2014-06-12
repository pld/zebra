(ns ona.viewer.views.projects
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.templates.base :only [base-template dashboard-items]]
        [ona.viewer.templates.forms :only [new-project-form]]
        [ona.viewer.templates.projects :only [project-forms project-settings]]
        [ring.util.response :only [redirect-after-post]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.project :as api]
            [ona.api.user :as api-user]
            [ona.api.dataset :as api-dataset]
            [ona.viewer.urls :as u]
            [ona.utils.time :as t]))

(defn- latest-submitted-form
  "Parses forms from all projects and returns form with latest submission time"
  [forms]
  (let [forms-w-intervals
        (for [form forms
              :let [last-submit (:last_submission_time form)]
              :when (not (nil? last-submit))]
          {(:formid form)
           {:form form
            :time (t/time->interval-from-now last-submit)}})]
    (if (> (count forms-w-intervals) 0)
      (let [all-forms-w-intervals (apply merge forms-w-intervals)
            latest-formid (key (apply min-key
                                      #(-> % val :time)
                                      all-forms-w-intervals))]
        (:form (get all-forms-w-intervals latest-formid))))))

(defn- all-submissions
  "Get all submission for dataset"
  ;; TODO  move functionality to api to reduce number of API calls
  [forms account]
  (map #(api-dataset/data account (:formid %)) forms))

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

(defn forms
  "Show the forms for a project."
  [account owner id]
  (let [project (api/get-project account owner id)
        forms (api/get-forms account owner id)
        profile (api-user/profile account)
        latest-form (latest-submitted-form forms)
        all-submissions (all-submissions forms account)]
    (base-template
     (u/project-forms id owner)
     account
     "Project Forms"
     (project-forms owner project forms profile latest-form all-submissions))))

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
        owner-url (make-url (str "users/" owner))
        data {:name (:name params)
              :owner owner-url}]
    (try+
     (let [project (api/create account data owner)]
       (redirect-after-post (u/project-settings project owner)))
     (catch vector? errors
       (new-project account errors)))))
