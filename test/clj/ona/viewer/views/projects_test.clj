(ns ona.viewer.views.projects-test
  (:use midje.sweet
        ona.viewer.views.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]]
        [ring.util.response :only [redirect-after-post]])
  (:require [ona.api.project :as api]
            [ona.api.organization :as api-org]
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
          (new-project account username []) => :something)))

(let [id :id
      project-name "project-name"
      username "username"
      fake-account {:username username}
      project {:id id :name project-name}
      forms [{:title "Test Form"
              :num_of_submissions 2}]]
  (fact "settings for a project shows project name"
        (settings fake-account username id) => (contains project-name)
        (provided
         (api/get-project fake-account username id) => project
         (api-org/all fake-account) => []))

  (facts "show for project"
         "should show project name"
         (show fake-account username id) => (contains project-name)
         (provided
          (api/get-project fake-account username id) => project
          (api/get-forms fake-account username id) => forms
          (h/profile-with-projects fake-account) => []
          (h/all-submissions forms fake-account) => []
          (api-org/all fake-account) => [])))
