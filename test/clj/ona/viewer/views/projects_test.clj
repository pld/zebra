(ns ona.viewer.views.projects-test
  (:use midje.sweet
        ona.viewer.views.projects
        [ona.api.io :only [make-url]])
  (:require [ona.api.project :as api]))

(fact "all returns the projects"
      (let [fake-project :project]
        (all :fake-account) => (contains (str fake-project))
        (provided
         (api/all :fake-account) => [fake-project])))

(fact "create shows new project"
      (let [username "username"
            account {:username username}
            project-name "new-project"
            params {:name project-name}
            data (assoc params :owner :url)]
        (create account params) => :something
        (provided
         (api/create account data) => :new-project
         (make-url "users/username") => :url
         (all account) => :something)))
