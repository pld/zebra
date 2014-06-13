(ns ona.viewer.views.organizations
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [project-details]]
        [ona.viewer.templates.forms :only [new-organization-form]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-datasets]
            [ona.api.project :as api-projects]
            [clojure.string :as string]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.organization :as org-templates]
            [ona.viewer.urls :as u]))

(defn all
  "Show all of the organizations for a user."
  [account]
  (let [organizations (api/all account)]
    (base/dashboard-items
      "Organizations"
      account
      "organizations/"
      organizations
      (new-organization-form))))

(defn create
  "Create a new organization."
  [account params]
  (let [org (string/replace
              (string/lower-case (:name params)) #" " "")
        data {:name (:name params)
              :org org}
        organization (api/create account data)]
    (all account)))

(defn profile
  "Retrieve the profile for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        teams (api/teams account org-name)
        members (api/members account org-name)
        project-details (project-details account org-name)]
    (base/base-template
      (u/org org)
      account
      (:name org)
      (org-templates/profile org members teams project-details))))

(defn teams
  "Retrieve the team for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        teams (api/teams account org-name)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/teams org teams))))

(defn team-info
  "Retrieve team-info for a specific team."
  [account org-name team-id]
  (let [org (api/profile account org-name)
        team-info (api/team-info account org-name team-id)
        team-members (api/team-members account org-name team-id)
        members-info (for [user team-members]
                       {:username user
                        :num-forms (count (api-datasets/public account user))})
        team-data {:team-id team-id
                   :team-info team-info
                   :members-info members-info}]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/team-info org team-data))))

(defn new-team
  "Show new-team form for organization."
  [account org-name]
  (let [org (api/profile account org-name)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/new-team org))))

(defn create-team
  "Create a new team"
  [account params]
  (let [org-name (:organization params)
        added-team (api/create-team account params)]
    (teams account org-name)))

(defn add-team-member
  "Add member to a team"
  [account params]
  (let [org-name (:org params)
        team-id (:teamid params)
        user {:username (:username params) :organization org-name}
        added-user (api/add-team-member account org-name team-id user)]
    (team-info account org-name team-id)))

(defn members
  "Retrieve the members for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        members (api/members account org-name)
        members-info (for [user members]
                       {:username user
                        :num-forms (count (api-datasets/public account user))})]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/members org members-info))))

(defn add-member
  "Add member to an organization"
  [account params]
  (let [org-name (:orgname params)
        member {:username (:username params)}
        added-user (api/add-member account org-name member)]
    (members account org-name)))
