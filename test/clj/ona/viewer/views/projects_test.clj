(ns ona.viewer.views.projects-test
  (:use midje.sweet
        ona.viewer.views.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]]
        [ring.util.response :only [redirect-after-post]])
  (:require [ona.api.project :as api]
            [ona.api.user :as api-user]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [ona.viewer.helpers.projects :as h]
            [ona.viewer.urls :as u]))

(facts "create new project"
       (let [username "username"
             account {:username username}
             project-name "new-project"
             name-hash {:name project-name}
             params (merge name-hash
                           {:owner username})
             project-id-hash {:id :id}]

         "Should go to settings on success"
         (let [redirect-url (u/project-settings username project-id-hash)]
           (create account params) => :something
           (provided
            (api/create account name-hash username) => project-id-hash
            (redirect-after-post redirect-url) => :something))

         "Should go to new on thrown error"
         (create account params) => :something
         (provided
          (api/create account name-hash username) =throws=> (slingshot-exception [])
          (new-project account []) => :something)))

(let [id :id
      project-name "project-name"
      username "username"
      fake-account {:username username}
      project {:id id :name project-name}]
  (fact "settings for a project shows project name"
        (settings fake-account username id) => (contains project-name)
        (provided
         (api/get-project fake-account username id) => project))

  (facts "forms for project"
         "Should show project name"
         (show fake-account username id) => (contains project-name)
         (provided
          (api/get-project fake-account username id) => project
          (api/get-forms fake-account username id) => [{:title "Test Form" :num_of_submissions 2}]
          (h/profile-with-projects fake-account) => []))

  (let [two-days-ago 2
        days-ago-2 (t/minus (l/local-now) (t/days two-days-ago))
        days-ago-2-str (f/unparse (f/formatters :date-time) days-ago-2)
        three-days-ago 3
        days-ago-3 (t/minus (l/local-now) (t/days three-days-ago))
        days-ago-3-str (f/unparse (f/formatters :date-time) days-ago-3)
        form {:formid 1
              :last_submission_time days-ago-2-str}
        forms [form
               {:formid 2
                :last_submission_time days-ago-3-str}]
        forms-with-empty [form {:formid 2}]]
    (facts "Should show latest sumbission"
           (h/latest-submitted-form forms) => form)

    (facts "Should show nothing if no forms"
           (h/latest-submitted-form []) => nil)

    (facts "Should ignore forms with no latest submission time"
           (h/latest-submitted-form forms-with-empty) => form)))
