(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-dataset]
            [ona.api.project :as api-projects]
            [ona.api.user :as api-user]
            [ona.viewer.urls :as u]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [ring.util.response :as response]))

(let [name "fake-org-name"
      fake-organization {:name name}
      username "username"
      account {:username username}
      team-name "Fake Team"
      fake-teams [{:id 1
                   :team {:name team-name}
                   :members [username]}]]
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
                                                       fake-teams) => [username]
         (api-projects/all account name) => [{:name "Fake Org"}]))

  (facts "teams"
         "should show organization teams"
         (teams account name) => (contains team-name)
         (provided
          (api/profile account name) => {:org "fake-org"}
          (api/teams account name) => fake-teams
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name
           fake-teams) => fake-teams)

         "should show leave button if user in team"
         (teams account name) => (contains "Leave")
         (provided
          (api/profile account name) => {:org "fake-org"}
          (api/teams account name) => fake-teams
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name
           fake-teams) => fake-teams)

         "should hide leave button if user in team but only owner"
         (teams account name) =not=> (contains "Leave")
         (provided
          (api/profile account name) => {:org "fake-org"}
          (api/teams account name) => fake-teams
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name
           fake-teams) => [{:id 1
                            :team {:name api/owners-team-name}
                            :members [username]}]))

  (fact "team-info shows info for a specific team"
        (team-info account name :team-id) => (contains "Fake Team" username :gaps-ok)
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/team-info account name :team-id) => {:name "Fake Team"}
         (api/team-members account name :team-id) => [username]
         ;; TODO uncomment when pulling profiles
         ;; (api-user/profile account username) => {:username username}
         (api-dataset/public account username) => [:fake-forms]
         (api/all account) => [{:name "Fake Org"}]
         (api/teams account name) => fake-teams
         (#'ona.viewer.views.organizations/all-members account
                                                       name
                                                       fake-teams) => []))

  (fact "new-team shows new team form"
        (new-team account name) => (contains "Create team")
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/all account) => [{:name "Fake Org"}]))

  (fact "create-team should create new team and show new team details"
        (let [params {:name "new fake team"
                      :organization name}
              new-team-id "42"
              new-team {:url (str "/new/team/url/" new-team-id)}]
          (create-team account params) => :updated-teamlist
          (provided
           (api/create-team account params) => new-team
           (u/org-team name new-team-id) => :url
           (response/redirect-after-post :url) => :updated-teamlist)))

  (fact "add-team member should add a user to a team"
        (let [user { :username "someuser" :organization name}
              team-id 1
              params (merge {:org name :teamid team-id} user)]
          (add-team-member account params) => :something
          (provided
           (api/add-team-member account name team-id user) => :new-member
           (response/redirect-after-post (u/org-team name team-id)) => :something)))

  (fact "members shows organization members"
        (members account name) => (contains username)
        (provided
         (api/profile account name) => {:name "Fake Org"}
         (api/teams account name) => fake-teams
         (#'ona.viewer.views.organizations/all-members account
                                                       name
                                                       fake-teams) => [username]
         ;; TODO uncomment when pulling profiles
         ;; (api-user/profile account username) => {:username username}
         (api-dataset/public account username) => [:fake-forms]
         (api/all account) => [{:name "Fake Org"}]))

  (fact "add-member should add members to organization"
        (add-member account name username) => :something
        (provided
         (api/add-member account name username) => :new-member
         (response/redirect-after-post (u/org-members name)) => :something))

  (facts "remove-member"
        "Should remove a member from an organization"
        (remove-member account name username) => :something
        (provided
         (api/remove-member account name username nil) => :new-member
         (response/redirect-after-post (u/org-members name)) => :something)

        "Should remove a member from a team"
        (let [team-id "1"]
          (remove-member account name username team-id) => :something
          (provided
           (api/remove-member account name username team-id) => :new-member
           (response/redirect-after-post (u/org-team name team-id)) => :something)))

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
