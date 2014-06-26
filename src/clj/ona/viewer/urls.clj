(ns ona.viewer.urls
  (:use [ona.utils.seq :only [remove-nil]]
        [ona.utils.string :only [url]]))

;; Datasets
(defmacro dataset-url
  "Create dataset-{name} method taking args owner, project-id, and dataset-id."
  [& title]
  `(intern *ns*
           (symbol (apply str (cons "dataset"
                                    (if-not (empty? [~@title]) ["-" ~@title]))))
           (fn [owner# project-id# dataset-id#]
             (url owner# project-id# dataset-id# ~@title))))

"Build dataset show link from dataset id."
(dataset-url)

"Build dataset delete link from dataset id."
(dataset-url "delete")

"Build dataset download link from dataset id."
(dataset-url "download")

"Build dataset metadata link from dataset and project id."
(dataset-url "metadata")

"Build dataset sharing link from dataset and project id."
(dataset-url "sharing")

"Build dataset settings link from dataset and project id."
(dataset-url "settings")

"Build dataset chart link from dataset and project id."
(dataset-url "chart")

"Build dataset photo link from dataset and project id."
(dataset-url "photo")

"Build dataset activity link from dataset and project id."
(dataset-url "activity")

"Build dataset table link from dataset and project id."
(dataset-url "table")

"Build dataset tags link from dataset and project id."
(dataset-url "tags")

"Build dataset move link from dataset-id and project-id"
(dataset-url "move")

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

(defn profile-settings
  "Build profile settings url from name."
  [name]
  (url name "settings"))

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

;; Search
(defn search
  "Build url for search"
  [owner]
  (url owner "search"))
