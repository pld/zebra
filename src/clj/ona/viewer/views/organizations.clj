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
  (let [organization (api/profile account org-name)
        orgs (api/all account)]
    (base/base-template
      "/organizations"
      (:username account)
      (:name organization)
      orgs
      (org-templates/profile organization))))

(defn teams
  "Retrieve the profile for an organization."
  [account org-name]
  (let [organization (api/profile account org-name)
        teams (api/teams account org-name)]
    (base/base-template
      "/organizations"
      (:username account)
      (:name organization)
      teams
      (org-templates/teams teams))))
