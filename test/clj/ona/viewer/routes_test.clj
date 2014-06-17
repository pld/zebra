(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes
        [ona.utils.string :only [url]])
  (:require [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.projects :as projects]
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
      session {:account :fake-account}]
  (facts "dataset routes"
         "GET dataset should parse account"
         (dataset-routes {:request-method :get
                          :uri (u/dataset dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account dataset-id project-id) => result)

         "GET table should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-table dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account dataset-id project-id :table) => result)

         "GET photo should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-photo dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account dataset-id project-id :photo) => result)

         "GET activity should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-activity dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account dataset-id project-id :activity) => result)

         "GET chart should pass context"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-chart dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/show :fake-account dataset-id project-id :chart) => result)


         "POST new dataset should call create"
         (dataset-routes {:request-method :post
                          :uri (u/project-new-dataset project-id owner)
                          :session session}) => (contains result)
         (provided
          (datasets/create :fake-account
                           nil
                           owner
                           project-id) => result)

         "GET delete should call delete"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-delete dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/delete :fake-account dataset-id) => result)

         "GET tags should call tags"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-tags dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/tags :fake-account dataset-id project-id) => result)

         "POST tags should call create-tags"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-tags dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/create-tags :fake-account dataset-id project-id nil) => result)

         "GET download should call download"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-download dataset-id)
                          :session session}) => (contains result)
         (provided
          (datasets/download :fake-account dataset-id :csv) => result)

         "GET sharing should call sharing"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-sharing dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/sharing :fake-account dataset-id project-id) => result)

         "POST sharing should call sharing-update"
         (dataset-routes {:request-method :post
                          :uri u/dataset-sharing-post
                          :session session}) => (contains result)
         (provided
          (datasets/sharing-update :fake-account {}) => result)

         "GET metadata should call metadata"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-metadata dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/metadata :fake-account dataset-id project-id) => result)

         "POST metadata should call update"
         (dataset-routes {:request-method :post
                          :uri (u/dataset-metadata dataset-id project-id)
                          :session session
                          :params {:description :description
                                   :title :title
                                   :tags :tags}}) => (contains result)
         (provided
          (datasets/update :fake-account
                           dataset-id
                           project-id
                           :title
                           :description
                           :tags) => result)

         "GET search should call home-page"
         (dataset-routes {:request-method :get
                          :uri "/search"
                          :session session
                          :params {:query :query}}) => (contains result)
         (provided
          (home/home-page :fake-account :query) => result)

         "GET dataset should call show-all"
         (dataset-routes {:request-method :get
                          :uri "/datasets"
                          :session session}) => (contains result)
         (provided
          (datasets/show-all :fake-account) => result)

         "GET move should call move-to-project"
         (dataset-routes {:request-method :get
                          :uri (u/dataset-move dataset-id project-id)
                          :session session}) => (contains result)
         (provided
          (datasets/move-to-project :fake-account dataset-id project-id) => result)



         )

  (facts "Project routes"
         "GET projects should call new-project"
         (project-routes {:request-method :get
                          :uri (u/project-new username)
                          :session session}) => (contains result)
         (provided
          (projects/new-project :fake-account username) => result)

         "GET show should call show"
         (project-routes {:request-method :get
                          :uri (u/project-show project-id username)
                          :session session}) => (contains result)
         (provided
          (projects/show :fake-account username project-id) => result)

         "GET settings should call settings"
         (project-routes {:request-method :get
                          :uri (u/project-settings {:id project-id} username)
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
                      :uri (u/org {:org org-name})
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
