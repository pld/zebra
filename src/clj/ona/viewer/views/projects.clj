(ns ona.viewer.views.projects
  (:use [hiccup core page]
        [ona.api.io :only [make-url]]
        [ona.viewer.views.partials :only [base]]
        [ona.viewer.views.templates :only [dashboard-items
                                           create-project-form]])
  (:require [ona.api.project :as api]))

(defn all [account]
  (let [projects (api/all account)]
    (dashboard-items
      "Projects"
      (:username account)
      (for [project projects]
        {:item-name (str project)})
      create-project-form)))

(defn create [account params]
  (let [owner (make-url (str "users/" (:username account)))
        data {:name (:name params)
              :owner owner}
        project (api/create account data)]
    (all account)))
