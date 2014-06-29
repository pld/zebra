(ns ona.viewer.helpers.projects-test
  (:use midje.sweet
        ona.viewer.helpers.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]])
  (:require [ona.api.project :as api]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(let [username :username
      account {:username username}
      project-id "12"
      days-ago 2
      days-ago-2 (t/minus (l/local-now) (t/days days-ago))
      days-ago-2-str (f/unparse (f/formatters :date-time) days-ago-2)
      date-created-str (f/unparse (f/formatters :rfc822) days-ago-2)
      three-days-ago 3
      days-ago-3 (t/minus (l/local-now) (t/days three-days-ago))
      days-ago-3-str (f/unparse (f/formatters :date-time) days-ago-3)
      form {:formid 1
            :last_submission_time days-ago-2-str}
      forms [form
             {:formid 2
              :last_submission_time days-ago-3-str}]
      forms-with-empty [form {:formid 2}]]
  (fact "get project details for and organizations projects"
        (project-details account username) =>
        (contains
         {:date-created date-created-str
          :last-modification nil
          :num-datasets 1
          :submissions "0 submissions"
          :project {:date_created days-ago-2-str
                    :date_modified days-ago-2-str
                    :name "Some project"
                    :url (str "http://someurl/" project-id)}})
        (provided
         (api/all account) => [{:date_created days-ago-2-str
                                :name "Some project"
                                :url "http://someurl/12"
                                :date_modified days-ago-2-str}]
         (api/get-forms account project-id) => [{}]))
  (facts "about latest-submitte-form"
         "Should show latest sumbission"
         (latest-submitted-form forms) => form

         "Should show nothing if no forms"
         (latest-submitted-form []) => nil

         "Should ignore forms with no latest submission time"
         (latest-submitted-form forms-with-empty) => form))
