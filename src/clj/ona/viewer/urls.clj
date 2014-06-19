(ns ona.viewer.urls
  (:use [ona.utils.seq :only [remove-nil]]
        [ona.utils.string :only [url]]))

;; Datasets
(defn dataset
  "Build dataset show link from dataset id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id))

(defn dataset-delete
  "Build dataset delete link from dataset id."
  [dataset-id]
  (url "dataset" dataset-id "delete"))

(defn dataset-download
  "Build dataset download link from dataset id."
  [dataset-id]
  (url "dataset" dataset-id "download"))

(defn dataset-metadata
  "Build dataset metadata link from dataset id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "metadata"))

(defn dataset-sharing
  "Build dataset sharing link from dataset id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "sharing"))

(def dataset-sharing-post (url "dataset" "sharing"))

(defn dataset-chart
  "Build dataset chart link from dataset and project id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "show/chart"))

(defn dataset-photo
  "Build dataset photo link from dataset and project id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "show/photo"))

(defn dataset-activity
  "Build dataset activity link from dataset and project id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "show/activity"))

(defn dataset-table
  "Build dataset table link from dataset and project id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "show/table"))

(defn dataset-tags
  "Build dataset tags link from dataset and project id."
  [dataset-id project-id]
  (url "dataset" dataset-id project-id "tags"))

(defn dataset-move
  "Build dataset move link from dataset-id and project-id"
  [dataset-id project-id]
  (url "dataset" "move" dataset-id project-id))


;; Organizatinos
(defn org
  "Build url for an organization."
  [org]
  (url "organizations" (:org org)))

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
       "team"
       team-id))

(defn org-members
  "Build the org members url."
  [org]
  (url "organizations" org "members"))

(defn org-new-team
  "Build the org new team url."
  [org]
  (url "organizations" org "new-team"))

(defn org-remove-member
  "Build the org remove member url."
  ([org username]
     (org-remove-member org username nil))
  ([org username team]
     (apply url (remove-nil ["organizations" org "remove" username team]))))

;; Profile
(defn profile
  "Build profile url from username."
  [username]
  (url "profile" username))

;; Projects
(defn project-show
  "Build the show project url from a project"
  [project-id owner]
  (url "project" owner project-id "show"))

(defn project-new-dataset
  "Build the project settings url from a project-id"
  [project-id owner]
  (url "project" owner project-id "new-dataset"))

(defn project-settings
  "Build the project settings url from a project"
  [project owner]
  (url "project" owner (:id project) "settings"))

(defn project-new
  "Build the url for a new project given owner."
  [owner]
  (url "project" owner))
