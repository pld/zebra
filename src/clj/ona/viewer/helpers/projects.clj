(ns ona.viewer.helpers.projects
  :require [ona.api.project :as api])

(defn project-details
  "Gets project details for an account and owner."
  [account owner]
  (let [projects (api/all account owner)]
    (for [project projects]
      {:project project
       :last-modification (-> project
                              :date_modified
                              t/date->days-ago-str)
       :no-of-datasets (count
                        (api/get-forms
                         account
                         owner
                         (s/last-url-param (:url project))))})))
