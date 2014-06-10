(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-dataset]
            [ona.api.project :as api-projects]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(let [name "fake-org-name"
      fake-organization {:name name}
      username "username"
      account {:username username}]
  (fact "all returns the organizations"
        (all :fake-account) => (contains name)
        (provided
         (api/all :fake-account) => [fake-organization]))

  (fact "create shows new organization"
        (let [organization-name "new-organization"
              params {:name organization-name
                      :org organization-name}]
          (create account params) => :something
          (provided
            (api/create account params) => :new-organization
            (all account) => :something)))

  (fact "profile shows organization detail"
        (profile account name) => (contains "Fake Org")
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/teams account name) => [{:name "Fake Team"}]
         (api/members account name) => [{:name "Fake Member"}]
         (api/all account) => [{:name "Fake Org"}]))

  (fact "teams shows organization teams"
        (teams account name) => (contains "Fake Team")
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/teams account name) => [{:name "Fake Team"}]
          (api/all account) => [{:name "Fake Org"}]))

  (fact "team-info shows info for a specific team"
        (team-info account name :team-id) => (contains "Fake Team" "member" :gaps-ok)
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/team-info account name :team-id) => {:name "Fake Team"}
          (api/team-members account name :team-id) => ["member"]
          (api-dataset/public account "member") => [:fake-forms]
          (api/all account) => [{:name "Fake Org"}]))

  (fact "new-team shows new team form"
        (new-team account name) => (contains "Create team")
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/all account) => [{:name "Fake Org"}]))

  (fact "create-team should create new team and show new team details"
        (let [params {:name "new fake team" :organization name}]
            (create-team account params) => :updated-teamlist
              (provided
                (api/create-team account params) => :new-team
                (teams account name) => :updated-teamlist)))

  (fact "add-team member should add a user to a team"
        (let [user { :username "someuser" :organization name}
              params (merge {:org name :teamid 1} user)]
          (add-team-member account params) => :something
          (provided
            (api/add-team-member account name 1 user) => :new-member
            (team-info account name 1) => :something)))

  (fact "members shows organization members"
        (members account name) => (contains "Fake Member")
        (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/members account name) => ["Fake Member"]
          (api-dataset/public account "Fake Member") => [:fake-forms]
          (api/all account) => [{:name "Fake Org"}]))

  (fact "add-member should add members to organization"
        (let [member { :username "someuser"}
               params (merge {:orgname name} member)]
          (add-member account params) => :something
          (provided
            (api/add-member account name member) => :new-member
            (members account name) => :something)))

  (facts "get project details for and organizations projects"
         (let [days-ago 2
               days-ago-2 (t/minus (l/local-now) (t/days days-ago))
               days-ago-2-str (f/unparse (f/formatters :date-time) days-ago-2)]
           (project-details account) => (contains
                                         {:last-modification (str days-ago
                                                                  " days")
                                          :no-of-datasets 1
                                          :project {:date_modified days-ago-2-str
                                                    :name "Some project"
                                                    :url "http://someurl/12"}})
           (provided
            (api-projects/all account) => [{:name "Some project"
                                            :url "http://someurl/12"
                                            :date_modified days-ago-2-str}]))))
