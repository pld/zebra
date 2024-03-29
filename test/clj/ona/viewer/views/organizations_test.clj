(ns ona.viewer.views.organizations-test
  (:use midje.sweet
        ona.viewer.views.organizations
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.organization :as api]
            [ona.api.dataset :as api-dataset]
            [ona.api.user :as api-user]
            [ona.viewer.urls :as u]
            [ring.util.response :as response]))

(let [name "fake-org-name"
      fake-organization {:name name}
      username "username"
      account {:username username}
      team-id "1"
      team-name "Fake Team"
      fake-teams [{:id team-id
                   :team {:name team-name}
                   :members [username]}]
      fake-owners-team [{:id team-id
                         :team {:name api/owners-team-name}
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
         (project-details account name) => [{:name "Fake Org"}]
         (api/all account) => []))

  (facts "teams"
         "should show organization teams"
         (teams account name) => (contains team-name)
         (provided
          (api/profile account name) => {:org "fake-org"}
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name) => fake-teams
          (api/all account) => [])

         "should show leave button if user in team"
         (teams account name) => (contains "Leave")
         (provided
          (api/profile account name) => {:org "fake-org"}
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name) => fake-teams
          (api/all account) => [])

         "should hide leave button if user in team but only owner"
         (teams account name) =not=> (contains "Leave")
         (provided
          (api/profile account name) => {:org "fake-org"}
          (#'ona.viewer.views.organizations/teams-with-details
           account
           name) => fake-owners-team
          (api/all account) => []))

  (facts "team-info"
         "should show info for a specific team and remove button"
         (team-info account name :team-id) => (contains "Fake Team"
                                                        "Remove"
                                                        username
                                                        :in-any-order
                                                        :gaps-ok)
         (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/team-info account name :team-id) => {:name "Fake Team"}
          (api/team-members account name :team-id) => [username]
          (api-user/profile account username) => {:username username}
          (api-dataset/public account username) => [:fake-forms]
          (api/all account) => [{:name "Fake Org"}]
          (api/teams account name) => fake-teams
          (#'ona.viewer.views.organizations/all-members account
                                                        name
                                                        fake-teams) => [])

         "should hide remove button if single owner"
         (team-info account name :team-id) =not=> (contains "Remove" :gaps-ok)
         (provided
          (api/profile account name) => {:name "Fake Org"}
          (api/team-info account name :team-id) => {:name api/owners-team-name}
          (api/team-members account name :team-id) => [username]
          (api-user/profile account username) => {:username username}
          (api-dataset/public account username) => [:fake-forms]
          (api/all account) => [{:name "Fake Org"}]
          (api/teams account name) => fake-owners-team
          (#'ona.viewer.views.organizations/all-members account
                                                        name
                                                        fake-owners-team) => []))

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
        (let [user {:username username :organization name}
              team-id 1]
          (add-team-member account name team-id username) => :something
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
         (api-user/profile account username) => {:username username}
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
          (api/single-owner? account name nil) => false
          (#'ona.viewer.views.organizations/teams-with-details account name)
          => [{:members [username]
               :id team-id}]
          (api/remove-member account name username) => nil
          (api/remove-member account name username team-id) => nil
          (response/redirect-after-post (u/org-members name)) => :something)

         "Should remove a member from a team"
         (remove-member account name username team-id) => :something
         (provided
          (api/single-owner? account name team-id) => false
          (api/remove-member account name username team-id) => nil
          (response/redirect-after-post (u/org-team name team-id)) => :something)

         "Should not remove last owner from a team"
         (remove-member account name username team-id) =>  "Cannot remove last owner."
         (provided
          (api/single-owner? account name team-id) => true)))
