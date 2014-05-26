(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]])
  (:require [ona.api.organization :as api]))

(let [name "fake-org-name"
      fake-organization {:name name}
      username "username"
      account {:username username}]
  (fact "all returns the organizations"
        (let []
          (all :fake-account) => (contains name)
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

  (fact "profile shows organization detail"
        (let [organization :fake-organization]
          (profile account name) => (contains "Fake Org")
          (provided
            (api/profile account name) => {:name "Fake Org"}
            (api/all account) => [{:name "Fake Org"}])))

  (fact "teams shows organization teams"
        (teams account name) => (contains "Fake Team")
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/teams account name) => [{:name "Fake Team"}]))

  (fact "members shows organization members"
        (members account name) => (contains "Fake Member")
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/members account name) => [{:name "Fake Member"}]))

  (fact "add member should add members to organization"
        (let [member { :username "someuser"}
               params (merge {:orgname name} member)]
          (add-member account params) => :something
          (provided
            (api/add-member account name member) => :new-member
            (members account name) => :something))))
