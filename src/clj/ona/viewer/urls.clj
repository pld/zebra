(ns ona.viewer.urls
  (:use [ona.utils.seq :only [remove-nil]]
        [ona.utils.string :only [url]]))

;; Datasets
(defmacro dataset-url
  "Build a URL with required owner, project-id, and dataset-id scope."
  [name & suffix]
  `(def ~name (fn [owner# project-id# dataset-id#]
                (url owner# project-id# dataset-id# ~@suffix))))

"Build dataset show link from dataset id."
(dataset-url dataset)

"Build dataset delete link from dataset id."
(dataset-url dataset-delete "delete")

"Build dataset download link from dataset id."
(dataset-url dataset-download "download")

"Build dataset metadata link from dataset and project id."
(dataset-url dataset-metadata "metadata")

"Build dataset sharing link from dataset and project id."
(dataset-url dataset-sharing "sharing")

"Build dataset settings link from dataset and project id."
(dataset-url dataset-settings "settings")

"Build dataset chart link from dataset and project id."
(dataset-url dataset-chart "chart")

"Build dataset photo link from dataset and project id."
(dataset-url dataset-photo "photo")

"Build dataset activity link from dataset and project id."
(dataset-url dataset-activity "activity")

"Build dataset table link from dataset and project id."
(dataset-url dataset-table "table")

"Build dataset tags link from dataset and project id."
(dataset-url dataset-tags "tags")

"Build dataset move link from dataset-id and project-id"
(dataset-url dataset-move "move")

(defn dataset-new
  "Build the new dataset for project URL."
  [owner project-id]
  (url owner project-id "new"))

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
