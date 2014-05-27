(ns ona.viewer.views.organizations
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]]
        [ona.viewer.templates.forms :only [new-organization-form]])
  (:require [ona.api.organization :as api]
            [clojure.string :as string]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.organization :as org-templates]))

(defn all
  "Show all of the organizations for a user."
  [account]
  (let [organizations (api/all account)]
    (base/dashboard-items
      "Organizations"
      (:username account)
      "organizations/"
      (for [organization organizations]
        {:item-id (:org organization) :item-name (:name organization)})
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
        orgs (api/all account)
        teams (api/teams account org-name)
        members (api/members account org-name)
        org-details {:org org :orgs orgs, :members members :teams teams}]
    (base/base-template
      "/organizations"
      (:username account)
      (:name org)
      orgs
      (org-templates/profile org-details))))

(defn teams
  "Retrieve the team for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        teams (api/teams account org-name)
        orgs (api/all account)]
    (base/base-template
      "/organizations"
      (:username account)
      (:name org)
      orgs
      (org-templates/teams org teams))))

(defn new-team
  "Show new-team form for organization."
  [account org-name]
  (let [org (api/profile account org-name)
        orgs (api/all account)]
    (base/base-template
      "/organizations"
      (:username account)
      (:name org)
      orgs
      (org-templates/new-team org))))

(defn create-team
  "Create a new team"
  [account params]
  (let [org-name (:organization params)
        team {:name (:name params) :organization (:organization params)}
        added-team (api/create-team account team)]
    (teams account org-name)))

(defn members
  "Retrieve the members for an organization."
  [account org-name]
  (let [org (api/profile account org-name)
        members (api/members account org-name)
        orgs (api/all account)]
    (base/base-template
      "/organizations"
      (:username account)
      (:name org)
      orgs
      (org-templates/members org members))))

(defn add-member
  "Add member to an organization"
  [account params]
  (let [org-name (:orgname params)
        member {:username (:username params)}
        added-user (api/add-member account org-name member)]
    (members account org-name)))
