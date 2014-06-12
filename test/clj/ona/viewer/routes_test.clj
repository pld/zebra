(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes)
  (:require [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.projects :as projects]
            [ona.viewer.views.organizations :as organizations]
            [ona.viewer.views.home :as home]))

(let [result {:body :something}
      session {:account :fake-account}]
  (fact "should parse account"
        (let [id "1"]
          (dataset-routes {:request-method :get
                        :uri (str "/dataset/" id)
                        :session session}) => (contains result)
          (provided
            (datasets/show :fake-account id) => result)))

  (fact "should parse account"
        (let [owner "ukanga"]
          (project-routes {:request-method :get
                        :uri (str "/projects/" owner)
                        :session session}) => (contains result)
          (provided
            (projects/all :fake-account owner) => result)))

  (fact "should parse account and params in project post"
        (let [username "username"
              params {:param-key :param-value
                      :owner username}]
          (project-routes {:request-method :post
                           :uri (str "/projects/" username)
                        :params params
                        :session session}) => (contains result)
          (provided
            (projects/create :fake-account params) => result)))

  (fact "should parse account"
        (org-routes {:request-method :get
                      :uri "/organizations"
                      :session session}) => (contains result)
        (provided
          (organizations/all :fake-account) => result))

  (fact "should parse account and params in organization post"
        (let [params {:param-key :param-value}]
          (org-routes {:request-method :post
                        :uri "/organizations"
                        :params params
                        :session session}) => (contains result)
          (provided
            (organizations/create :fake-account params) => result)))

  (fact "should parse account in organization profile"
        (let [name "orgname"]
          (org-routes {:request-method :get
                        :uri (str "/organizations/" name)
                        :session session}) => (contains result)
          (provided
            (organizations/profile :fake-account name) => result))))
