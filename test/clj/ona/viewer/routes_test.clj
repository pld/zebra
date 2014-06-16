(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes
        [ona.utils.string :only [url]])
  (:require [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.projects :as projects]
            [ona.viewer.views.organizations :as organizations]
            [ona.viewer.views.home :as home]
            [ona.viewer.urls :as u]))

(let [result {:body :something}
      session {:account :fake-account}]
  (facts "Dataset routes"
         "Should parse account"
         (let [id "1"]
           (dataset-routes {:request-method :get
                            :uri (str "/dataset/" id)
                            :session session}) => (contains result)
           (provided
            (datasets/show :fake-account id) => result))

         "Should parse args for new dataset"
         (let [id "1"
               owner "owner"]
           (dataset-routes {:request-method :post
                            :uri (str "/project/"
                                      owner
                                      "/"
                                      id
                                      "/new-dataset")
                            :session session}) => (contains result)
           (provided
            (datasets/create :fake-account nil owner id) => result)))

  (facts "Project routes"
         "Should parse account"
         (let [owner "owner"]
           (project-routes {:request-method :get
                            :uri (str "/projects/" owner)
                            :session session}) => (contains result)
           (provided
            (projects/all :fake-account owner) => result))

         "Should parse account and params in project post"
         (let [username "username"
               params {:param-key :param-value
                       :owner username}]
           (project-routes {:request-method :post
                            :uri (str "/projects/" username)
                            :params params
                            :session session}) => (contains result)
           (provided
            (projects/create :fake-account params) => result)))

  (facts "Organization routes"
         "Should parse account"
         (org-routes {:request-method :get
                      :uri "/organizations"
                      :session session}) => (contains result)
         (provided
          (organizations/all :fake-account) => result)

         "Should parse account and params in organization post"
         (let [params {:param-key :param-value}]
           (org-routes {:request-method :post
                        :uri "/organizations"
                        :params params
                        :session session}) => (contains result)
           (provided
            (organizations/create :fake-account params) => result))

         "Should parse account in organization profile"
         (let [name "orgname"]
           (org-routes {:request-method :get
                        :uri (str "/organizations/" name)
                        :session session}) => (contains result)
           (provided
            (organizations/profile :fake-account name) => result))

         "Should parse remove member"
         (let [name "orgname"
               member-username "membername"]
           (org-routes {:request-method :post
                        :uri (u/org-remove-member name member-username)
                        :session session}) => (contains result)
           (provided
            (organizations/remove-member :fake-account
                                         name
                                         member-username) => result))

         "Should parse remove member with team"
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
