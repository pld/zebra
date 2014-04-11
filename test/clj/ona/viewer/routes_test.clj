(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes)
  (:require [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.projects :as projects]
            [ona.viewer.views.organizations :as organizations]))

(let [result {:body :something}
      session {:account :fake-account}]
  (fact "should parse account"
        (let [id "1"]
          (main-routes {:request-method :get
                        :uri (str "/dataset/" id)
                        :session session}) => (contains result)
          (provided
           (datasets/dataset :fake-account id) => result)))

  (fact "should parse account"
        (main-routes {:request-method :get
                      :uri "/projects"
                      :session session}) => (contains result)
        (provided
         (projects/all :fake-account) => result))

  (fact "should parse account and params in project post"
        (let [params {:param-key :param-value}]
          (main-routes {:request-method :post
                        :uri "/projects"
                        :params params
                        :session session}) => (contains result)
          (provided
           (projects/create :fake-account params) => result)))

  (fact "should parse account"
        (main-routes {:request-method :get
                      :uri "/organizations"
                      :session session}) => (contains result)
        (provided
         (organizations/all :fake-account) => result)))
