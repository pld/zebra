(ns ona.viewer.views.projects-test
  (:use midje.sweet
        ona.viewer.views.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]]
        [ring.util.response :only [redirect-after-post]])
  (:require [ona.api.project :as api]))

(fact "all returns the projects"
      (let [fake-project :project]
        (all :fake-account) => (contains (str fake-project))
        (provided
         (api/all :fake-account) => [fake-project])))

(facts "create new project"
       (let [username "username"
             account {:username username}
             project-name "new-project"
             params {:name project-name}
             data (assoc params :owner :url)]

         "Should go to settings on success"
         (let [redirect-url (str "/project/" :id "/settings")]
           (create account params) => :something
           (provided
            (api/create account data) => {:id :id}
            (make-url "users/username") => :url
            (redirect-after-post redirect-url) => :something))

         "Should go to new on thrown error"
         (create account params) => :something
         (provided
          (api/create account data) =throws=> (slingshot-exception [])
          (make-url "users/username") => :url
          (new-project account []) => :something)))

(let [id :id
      project-name "project-name"
      username "username"
      fake-account {:username username}
      project {:id id :name project-name}]
  (fact "settings for a project shows project name"
        (settings fake-account id) => (contains project-name)
        (provided
         (api/get-project fake-account id) => project))

  (facts "forms for project"
         "Should show project name"
         (forms fake-account id) => (contains project-name)
         (provided
          (api/get-project fake-account id) => project)))
