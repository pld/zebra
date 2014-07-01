(ns ona.viewer.helpers.projects
  (:use [clj-http.client :only [with-connection-pool]])
  (:require [ona.api.project :as api]
            [ona.api.dataset :as api-dataset]
            [ona.api.user :as api-user]
            [ona.utils.string :as s]
            [ona.utils.time :as t]
            [ona.utils.numeric :as n]))

(defn latest-submitted-form
  "Parses forms from all projects and returns form with latest submission time"
  [forms]
  (let [forms-w-intervals
        (for [form forms
              :let [last-submit (:last_submission_time form)]
              :when (not (nil? last-submit))]
          {(:formid form)
            {:form form
             :time (t/time->interval-from-now last-submit)}})]
    (if (> (count forms-w-intervals) 0)
      (let [all-forms-w-intervals (apply merge forms-w-intervals)
            latest-formid (key (apply min-key
                                      #(-> % val :time)
                                      all-forms-w-intervals))]
        (:form (get all-forms-w-intervals latest-formid))))))

(defn all-submissions
  "Get all submission for dataset"
  ;; TODO  move functionality to api to reduce number of API calls
  [forms account]
  (map #(api-dataset/data account (:formid %)) forms))

(defn project-details
  "Gets project details for an account and owner."
  [account owner]
  (let [projects (api/all account)]
    (with-connection-pool {:threads 10 :default-per-route 10}
      (for [project projects]
        (let [forms (api/get-forms account (s/last-url-param (:url project)))
              latest-form (latest-submitted-form forms)
              all-submissions []
              ;; TODO after performance fixes uncomment
              ;; (all-submissions forms account)
              ]
          {:forms forms
           :project project
           :date-created (t/format-date (:date_created project) :rfc822)
           :last-modification (-> latest-form
                                  :last_submission_time
                                  t/date->days-ago-str)
           :submissions (n/pluralize-number (-> all-submissions flatten count)
                                            "submission")
           :num-datasets (count forms)})))))

(defn profile-with-projects
  "Get the user profile and a list of their projects."
  [account]
  (merge (api-user/profile account)
         {:projects (api/all account)}))
