(ns ona.viewer.urls
  (:use [ona.utils.seq :only [remove-nil]]
        [ona.utils.string :only [url]]))

;; Datasets
(defn dataset
  "Build dataset show link from dataset id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id))

(defn dataset-delete
  "Build dataset delete link from dataset id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "delete"))

(defn dataset-download
  "Build dataset download link from dataset id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "download"))

(defn dataset-metadata
  "Build dataset metadata link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "metadata"))

(defn dataset-new
  "Build the new dataset for project URL."
  [owner project-id]
  (url owner project-id "new"))

(defn dataset-sharing
  "Build dataset sharing link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "sharing"))

(defn dataset-settings
  "Build dataset settings link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "settings"))

(defn dataset-chart
  "Build dataset chart link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "chart"))

(defn dataset-photo
  "Build dataset photo link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "photo"))

(defn dataset-activity
  "Build dataset activity link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "activity"))

(defn dataset-table
  "Build dataset table link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "table"))

(defn dataset-tags
  "Build dataset tags link from dataset and project id."
  [owner project-id dataset-id]
  (url owner project-id dataset-id "tags"))

(defn dataset-move
  "Build dataset move link from dataset-id and project-id"
  [owner project-id dataset-id]
  (url owner project-id dataset-id "move"))


;; Organizations
(defn org
  "Build url for an organization."
  [org]
  (url (:org org)))

(defn org-teams
  "Build the teams url for an organization."
  [org]
  (url "organizations"
       org
       "teams"))

(defn org-team
  "Build the team url for an organization team."
  [org team-id]
  (url "organizations"
       org
       "teams"
       team-id))

(defn org-members
  "Build the org members url."
  [org]
  (url "organizations" org "members"))

(defn org-new-team
  "Build the org new team url."
  [org]
  (url "organizations" org "teams/new"))

(defn org-remove-member
  "Build the org remove member url."
  ([org username]
     (org-remove-member org username nil))
  ([org username team]
     (apply url (remove-nil ["organizations" org "remove" username team]))))

;; Profile
(defn profile
  "Build profile url from name."
  [name]
  (url name))

;; Projects
(defn project-show
  "Build the show project url from a project"
  [owner project-id]
  (url owner project-id))

(defn project-settings
  "Build the project settings url from a project"
  [owner project]
  (url owner (:id project) "settings"))

(defn project-new
  "Build the url for a new project given owner."
  [owner]
  (url owner "new"))
