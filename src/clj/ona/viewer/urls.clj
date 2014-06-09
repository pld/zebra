(ns ona.viewer.urls)

;; Datasets
(defn dataset
  "Build dataset show link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id))

(defn dataset-delete
  "Build dataset delete link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/delete"))

(defn dataset-download
  "Build dataset download link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/download"))

(defn dataset-metadata
  "Build dataset metadata link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/metadata"))

(defn dataset-sharing
  "Build dataset sharing link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/sharing"))

(def dataset-sharing-post "dataset/sharing")

(defn dataset-chart
  "Build dataset chart link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/show/chart"))

(defn dataset-photo
  "Build dataset photo link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/show/photo"))

(defn dataset-activity
  "Build dataset activity link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/show/activity"))


(defn dataset-table
  "Build dataset table link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/show/table"))

(defn dataset-tags
  "Build dataset tags link from dataset id."
  [dataset-id]
  (str "/dataset/" dataset-id "/tags"))

;; Organizatinos
(defn org
  "Build url for an organization."
  [org]
  (str "/organizations/" (:org org)))

;; Profile
(defn profile
  "Build profile url from username."
  [username]
  (str "/profile/" username))

;; Projects
(defn project-forms
  "Build the project forms url from a project"
  [project]
  (str "/project/" (:id project) "/forms"))

(defn project-new-dataset
  "Build the project settings url from a project-id"
  [project-id]
  (str "project/" project-id "/new-dataset"))

(defn project-settings
  "Build the project settings url from a project"
  [project]
  (str "/project/" (:id project) "/settings"))

(def project-new "/project")
