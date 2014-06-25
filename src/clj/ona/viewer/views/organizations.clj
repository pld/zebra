(ns ona.viewer.views.organizations
  (:use [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-datasets]
            [ona.api.project :as api-projects]
            [ona.api.user :as api-user]
            [clojure.string :as string]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.organization :as template]
            [ona.viewer.urls :as u]
            [ona.utils.string :as s]
            [ring.util.response :as response]))

(defn- teams-with-details
  "Add IDs and members to teams list hashes."
  ([account org-name]
     (teams-with-details account org-name (api/teams account org-name)))
  ([account org-name teams]
      (for [team teams
            :let [id (-> team :url s/last-url-param)]]
        {:id id
         :members (api/team-members account
                                    org-name
                                    id)
         :team team})))

(defn- remove-member-from-all-teams
  [account org-name member-username]
  (api/remove-member account org-name member-username)
  (doall
   (for [team (teams-with-details account org-name)
         :when (some #{member-username} (:members team))]
     (api/remove-member account org-name member-username (:id team)))))

(defn- all-members
  ([account org-name teams]
     (all-members (teams-with-details account org-name teams)))
  ([team-details]
     (flatten (map :members team-details))))

(defn- info-for-users
  [account members]
  (for [username members
        :let [profile (api-user/profile account username)]]
    {:profile profile
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
      (template/new))))

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
  ([account org-name]
     (profile account org-name (api/profile account org-name)))
  ([account org-name org]
     (let [teams (api/teams account org-name)
           members (all-members account org-name teams)
           project-details (project-details account org-name)]
       (base/base-template
        (u/org org)
        account
        (:name org)
        (template/profile org
                               members
                               teams
                               project-details
                               (some #{(:username account)}
                                     members))))))

(defn teams
  "Retrieve the team for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        team-details (teams-with-details account org-name)
        members (all-members team-details)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (template/show-teams (:org org)
                                team-details
                                members
                                (:username account)))))

(defn team-info
  "Retrieve team-info for a specific team."
  [account org-name team-id]
  (let [org (api/profile account org-name)
        team-info (api/team-info account org-name team-id)
        members (api/team-members account org-name team-id)
        members-info (info-for-users account members)
        all-teams (api/teams account org-name)
        all-members (all-members account org-name all-teams)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (org-templates/team-info (:org org)
                               team-id
                               team-info
                               members-info
                               all-teams
                               all-members))))

(defn new-team
  "Show new-team form for organization."
  [account org-name]
  (let [org (api/profile account org-name)]
    (base/base-template
      "/organizations"
      account
      (:name org)
      (template/new-team org))))

(defn create-team
  "Create a new team"
  [account params]
  (let [org-name (:organization params)
        added-team (api/create-team account params)
        id (-> added-team :url s/last-url-param)]
    (response/redirect-after-post (u/org-team org-name id))))

(defn add-team-member
  "Add member to a team"
  [account org-name team-id username]
  (let [user {:username username :organization org-name}]
    (api/add-team-member account org-name team-id user)
    (response/redirect-after-post (u/org-team org-name team-id))))

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
      org-name
      (template/members org-name members-info teams))))

(defn add-member
  "Add member to an organization."
  [account org-name member-username]
  (let [added-user (api/add-member account org-name member-username)]
    (response/redirect-after-post (u/org-members org-name))))

(defn remove-member
  "Remove a member from an organization."
  ([account org-name member-username]
     (remove-member account org-name member-username nil))
  ([account org-name member-username team-id]
     (if (api/single-owner? account org-name team-id)
       ;; TODO render a real error page.
       "Cannot remove last owner."
       (if team-id
         (do
           (api/remove-member account org-name member-username team-id)
           (response/redirect-after-post (u/org-team org-name team-id)))
         (do
           (remove-member-from-all-teams account org-name member-username)
           (response/redirect-after-post (u/org-members org-name)))))))
