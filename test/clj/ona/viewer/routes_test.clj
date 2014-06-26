(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes
        [ona.utils.string :only [url]])
  (:require [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.projects :as projects]
            [ona.viewer.views.profiles :as profiles]
            [ona.viewer.views.organizations :as organizations]
            [ona.viewer.views.home :as home]
            [ona.viewer.urls :as u]))

(let [dataset-id "7"
      project-id "42"
      owner "owner"
      org-name "orgname"
      team-id "67"
      username "username"
      result {:body :something}
      session {:account :fake-account}
      parsed-params {:project-id project-id
                     :dataset-id dataset-id
                     :owner owner}]
  (facts "main routes"
         "GET / should call home-page"
         (main-routes {:request-method :get
                       :uri "/"
                       :session session}) => (contains result)
         (provided
          (home/home-page :fake-account) => result))

  (facts "user routes"
         "GET join should call sign-up"
         (user-routes {:request-method :get
                       :uri "/join"}) => (contains result)
         (provided
          (profiles/sign-up) => result)

         "POST join should call submit-sign-up"
         (user-routes {:request-method :post
                       :uri "/join"}) => (contains result)
         (provided
          (profiles/submit-sign-up {}) => result)

         "POST login should call submit-login"
         (user-routes {:request-method :post
                       :uri "/login"}) => (contains result)
         (provided
          (accounts/submit-login {}) => result)

         "GET logout should call logout"
         (user-routes {:request-method :get
                       :uri "/logout"}) => (contains result)
         (provided
          (accounts/logout) => result)

         "GET profile with username should call user-profile"
         (user-routes {:request-method :get
                       :uri (u/profile username)
                       :session session}) => (contains result)
         (provided
          (profiles/profile :fake-account username) => result)

         "GET profile with username and settings should call user-profile with settings true"
         (user-routes {:request-method :get
                       :uri (u/profile-settings username)
                       :session session}) => (contains result)
         (provided
           (profiles/profile :fake-account username true) => result)

         "POST profile settings should call update profile"
         (let [params {:username username
                       :country "fake-country"
                       :owner username}]
           (user-routes {:request-method :post
                         :uri (u/profile-settings username)
                         :session session
                         :params params}) => (contains result)
           (provided
             (profiles/update :fake-account params) => result))


         "GET search should call home-page"
         (user-routes {:request-method :get
                          :uri (str "/" username "/search")
                          :session session
                          :params {:query :query}}) => (contains result)
         (provided
           (home/home-page :fake-account :query) => result))

  (facts "dataset routes"
         "GET dataset should parse account"
         (dataset-routes {:request-method :get
                          :uri (u/dataset owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account owner project-id dataset-id) => result)

         "GET table should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-table owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account owner project-id dataset-id :table) => result)

         "GET photo should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-photo owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account owner project-id dataset-id :photo) => result)

         "GET activity should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-activity owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account owner project-id dataset-id :activity) => result)

         "GET chart should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-chart owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account owner project-id dataset-id :chart) => result)

         "POST new dataset should call create"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-new owner project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/create :fake-account
                           owner
                           project-id
                           nil) => result)

         "GET delete should call delete"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-delete owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/delete :fake-account owner project-id dataset-id) => result)

         "GET tags should call tags"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-tags owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/tags :fake-account owner project-id dataset-id) => result)

         "POST tags should call create-tags"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-tags owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/create-tags :fake-account
                                owner
                                project-id
                                dataset-id
                                nil) => result)

         "GET download should call download"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-download owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/download :fake-account owner project-id dataset-id :csv) => result)

         "GET settings should call settings"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-settings owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/settings :fake-account owner project-id dataset-id) => result)

         "GET sharing should call sharing"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-sharing owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/sharing :fake-account owner project-id dataset-id) => result)

         "POST sharing should call sharing-update"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-sharing owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/sharing-update :fake-account owner parsed-params) => result)

         "GET settings should call settings"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-settings owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
           (datasets/settings :fake-account owner project-id dataset-id) => result)

         "POST setttings should call settings-update"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-settings owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/settings-update :fake-account parsed-params) => result)

         "GET metadata should call metadata"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-metadata owner dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/metadata :fake-account owner dataset-id project-id) => result)

         "POST metadata should call update"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-metadata owner dataset-id project-id)
                          :session session
                          :params {:description :description
                                   :title :title
                                   :tags :tags}}) => (contains result)
         (provided
          (datasets/update :fake-account
                           owner
                           dataset-id
                           project-id
                           :title
                           :description
                           :tags) => result)

         "GET move should call move-to-project"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-move owner project-id dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/move-to-project :fake-account
                                    owner
                                    project-id
                                    dataset-id) => result))

  (facts "Project routes"
         "GET projects should call new-project"
         (project-routes {:request-method :get
                          :uri (u/project-new username)
                          :session session}) => (contains result)
         (provided
          (projects/new-project :fake-account username) => result)

         "GET show should call show"
         (project-routes {:request-method :get
                          :uri (u/project-show username project-id)
                          :session session}) => (contains result)
         (provided
          (projects/show :fake-account username project-id) => result)

         "GET settings should call settings"
         (project-routes {:request-method :get
                          :uri (u/project-settings username {:id project-id})
                          :session session}) => (contains result)
         (provided
          (projects/settings :fake-account username project-id) => result)

         "POST projects should call create"
         (let [username "username"
               params {:param-key :param-value
                       :owner username}]
           (project-routes {:request-method :post
                            :uri (u/project-new username)
                            :params params
                            :session session}) => (contains result)
           (provided
            (projects/create :fake-account params) => result)))

  (facts "Organization routes"
         "GET organizations should call all"
         (org-routes {:request-method :get
                      :uri "/organizations"
                      :session session}) => (contains result)
         (provided
          (organizations/all :fake-account) => result)

         "POST organizations should call create"
         (let [params {:param-key :param-value}]
           (org-routes {:request-method :post
                        :uri "/organizations"
                        :params params
                        :session session}) => (contains result)
           (provided
            (organizations/create :fake-account params) => result))

         "GET organization by name should call profile"
         (org-routes {:request-method :get
                      :uri (str "/organizations/" org-name)
                      :session session}) => (contains result)
         (provided
          (organizations/profile :fake-account org-name) => result)

         "GET teams should call teams"
         (org-routes {:request-method :get
                      :uri (u/org-teams org-name)
                      :session session}) => (contains result)
         (provided
          (organizations/teams :fake-account org-name) => result)

         "GET team by id should call team-info"
         (org-routes {:request-method :get
                      :uri (u/org-team org-name team-id)
                      :session session}) => (contains result)
         (provided
          (organizations/team-info :fake-account org-name team-id) => result)

         "POST team by id should call add-team-member"
         (org-routes {:request-method :post
                      :uri (u/org-team org-name team-id)
                      :session session
                      :params {:org org-name
                               :teamid team-id
                               :username username}}) => (contains result)
         (provided
          (organizations/add-team-member :fake-account
                                         org-name
                                         team-id
                                         username) => result)

         "GET new-team should call new-team"
         (org-routes {:request-method :get
                      :uri (u/org-new-team org-name)
                      :session session}) => (contains result)
         (provided
          (organizations/new-team :fake-account org-name) => result)

         "POST new-team should call create-team"
         (org-routes {:request-method :post
                      :uri (u/org-new-team org-name)
                      :session session}) => (contains result)
         (provided
          (organizations/create-team :fake-account {:name org-name}) => result)

         "GET members should call members"
         (org-routes {:request-method :get
                      :uri (u/org-members org-name)
                      :session session}) => (contains result)
         (provided
          (organizations/members :fake-account org-name) => result)

         "POST members should call add-members"
         (org-routes {:request-method :post
                      :uri (u/org-members org-name)
                      :session session
                      :params {:orgname org-name
                               :username :member-username}}) => (contains result)
         (provided
          (organizations/add-member :fake-account
                                    org-name
                                    :member-username) => result)

         "POST remove with member should call remove-member"
         (let [name "orgname"
               member-username "membername"]
           (org-routes {:request-method :post
                        :uri (u/org-remove-member name member-username)
                        :session session}) => (contains result)
           (provided
            (organizations/remove-member :fake-account
                                         name
                                         member-username) => result))

         "POST remove with member and team should call remove-member"
         (let [name "orgname"
               member-username "membername"
               team-id "5"]
           (org-routes {:request-method :post
                        :uri (u/org-remove-member name member-username team-id)
                        :session session}) => (contains result)
           (provided
            (organizations/remove-member :fake-account
                                         name
                                         member-username
                                         team-id) => result))))
