(ns ona.viewer.helpers.projects-test
  (:use midje.sweet
        ona.viewer.helpers.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]])
  (:require [ona.api.project :as api]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(fact "get project details for and organizations projects"
      (let [username :username
            account {:username username}
            project-id "12"
            days-ago 2
            days-ago-2 (t/minus (l/local-now) (t/days days-ago))
            days-ago-2-str (f/unparse (f/formatters :date-time) days-ago-2)
            date-created-str (f/unparse (f/formatters :rfc822) days-ago-2)]
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
         (api/all account username) => [{:date_created days-ago-2-str
                                         :name "Some project"
                                         :url "http://someurl/12"
                                         :date_modified days-ago-2-str}]
         (api/get-forms account username project-id) => [{}])))
