(ns ona.viewer.views.projects
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.project :as api]))

(defn all [account]
  (let [projects (api/all account)]
    (base
     [:form {:action "/projects" :method "post"}
      [:input {:type "text" :name "name"}]
      [:input {:type "submit" :value "Create Project"}]]
     (for [project projects]
       [:p (str project)]))))

(defn create [account params]
  (let [owner (make-url (str "users/" (:username account)))
        data {:name (:name params)
              :owner owner}
        project (api/create account data)]
    (all account)))
