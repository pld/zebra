(ns ona.viewer.views.organizations
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [project-details]]
        [ona.viewer.templates.forms :only [new-organization-form]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-datasets]
            [ona.api.project :as api-projects]
            [ona.api.user :as api-user]
            [clojure.string :as string]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.organization :as org-templates]
            [ona.viewer.urls :as u]
            [ona.utils.string :as s]))

(defn- all-members
  [account org-name teams]
  (flatten (map #(api/team-members account
                                  org-name
                                  (-> % :url s/last-url-param))
                teams)))

(defn- info-for-users
  [account members]
  (for [username members]
    {:profile (api-user/profile account username)
     :num-forms (count (api-datasets/public account username))}))

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
        members (all-members account org-name teams)
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
        teams (api/teams account org-name)
        members (all-members account org-name teams)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/teams (:org org) teams members))))

(defn team-info
  "Retrieve team-info for a specific team."
  [account org-name team-id]
  (let [org (api/profile account org-name)
        team-info (api/team-info account org-name team-id)
        members (api/team-members account org-name team-id)
        members-info (info-for-users account members)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/team-info org team-id team-info members-info))))

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
        teams (api/teams account org-name)
        members (all-members account org-name teams)
        members-info (info-for-users account members)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/members org members-info teams))))

(defn add-member
  "Add member to an organization"
  [account params]
  (let [org-name (:orgname params)
        member {:username (:username params)}
        added-user (api/add-member account org-name member)]
    (members account org-name)))
