(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-dataset]
            [ona.api.project :as api-projects]
            [ona.api.user :as api-user]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]))

(let [name "fake-org-name"
      fake-organization {:name name}
      username "username"
      account {:username username}
      fake-teams [{:name "Fake Team"}]]
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
         (api/teams account name) => fake-teams
         (#'ona.viewer.views.organizations/all-members account
                                                       name
                                                       fake-teams) => ["Fake Member"]
         (api-projects/all account name) => [{:name "Fake Org"}]))

  (fact "teams shows organization teams"
        (teams account name) => (contains "Fake Team")
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/teams account name) => fake-teams
         (api/all account) => [{:name "Fake Org"}]))

  (fact "team-info shows info for a specific team"
        (team-info account name :team-id) => (contains "Fake Team" username :gaps-ok)
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/team-info account name :team-id) => {:name "Fake Team"}
         (api/team-members account name :team-id) => [username]
         (api-user/profile account username) => {:username username}
         (api-dataset/public account username) => [:fake-forms]
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
        (members account name) => (contains username)
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/teams account name) => fake-teams
         (#'ona.viewer.views.organizations/all-members account
                                                       name
                                                       fake-teams) => [username]
         (api-user/profile account username) => {:username username}
         (api-dataset/public account username) => [:fake-forms]
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
               days-ago-2-str (f/unparse (f/formatters :date-time) days-ago-2)
               date-created-str (f/unparse (f/formatters :rfc822) days-ago-2)]
           (project-details account username) =>
           (contains

            {:date-created date-created-str
             :last-modification nil
             :num-datasets 1
             :submissions "1 submission"
             :project {:date_created days-ago-2-str
                       :date_modified days-ago-2-str
                       :name "Some project"
                       :url "http://someurl/12"}})
           (provided
            (api-projects/all account username) => [{:date_created days-ago-2-str
                                                     :name "Some project"
                                                     :url "http://someurl/12"
                                                     :date_modified days-ago-2-str}]))))
