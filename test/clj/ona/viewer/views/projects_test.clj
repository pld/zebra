(ns ona.viewer.views.projects-test
  (:use midje.sweet
        ona.viewer.views.projects
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]])
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
         (create account params) => :something
         (provided
          (api/create account data) => :new-project
          (make-url "users/username") => :url
          (settings account :new-project) => :something)

         "Should go to new on thrown error"
         (create account params) => :something
         (provided
          (api/create account data) =throws=> (slingshot-exception [])
          (make-url "users/username") => :url
          (new-project account []) => :something)))
