(ns ona.viewer.views.organizations
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]]
        [ona.viewer.views.templates :only [dashboard-items
                                           new-organization-form]])
  (:require [ona.api.organization :as api]
            [clojure.string :as string]))

(defn all
  "Show all of the organizations for a user."
  [account]
  (let [organizations (api/all account)]
    (dashboard-items
      "Organizations"
      (:username account)
      "organizations/"
      (for [organization organizations]
        {:item-id (:org organization) :item-name (:name organization)})
      new-organization-form)))

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
  (let [organization (api/profile account org-name)]
    (dashboard-items
      (:name organization)
      (:username account)
      nil
      (for [org_detail organization]
        {:item-name (str org_detail)})
      nil)))
