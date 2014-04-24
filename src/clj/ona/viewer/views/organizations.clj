(ns ona.viewer.views.organizations
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.organization :as api]
            [clojure.string :as string]))

(defn all
  "Show all of the organizations for a user."
  [account]
  (let [organizations (api/all account)]
    (base
      [:form {:action "/organizations" :method "post"}
       [:input {:type "text" :name "name"}]
       [:input {:type "submit" :value "Create Organization"}]]
      (for [organization organizations]
        [:p [:a
            {:href (str "/organizations/" (:org organization))}
             (:name organization)]]))))

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
    (base
     (for [org_detail organization]
       [:p (str org_detail)]))))
