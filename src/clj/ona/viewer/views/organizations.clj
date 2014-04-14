(ns ona.viewer.views.organizations
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.organization :as api]))

(defn all [account]
  (let [organizations (api/all account)]
    (base
     [:form {:action "/organizations" :method "post"}
      [:input {:type "text" :name "name"}]
      [:input {:type "submit" :value "Create Organization"}]]
     (for [organization organizations]
       [:p (str organization)]))))
(defn create [account params]
  (let [data {:name (:name params)
              :org (:name params)}
        organization (api/create account data)]
    (all account)))
