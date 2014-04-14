(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]])
  (:require [ona.api.organization :as api]))

(fact "all returns the organizations"
      (let [fake-organization :organization]
        (all :fake-account) => (contains (str fake-organization))
        (provided
         (api/all :fake-account) => [fake-organization])))

(fact "create shows new organization"
      (let [username "username"
            account {:username username}
            organization-name "new-organization"
            params {:name organization-name
                    :org organization-name}]
        (create account params) => :something
        (provided
         (api/create account params) => :new-organization
         (all account) => :something)))
