(ns ona.viewer.api-test
  (:use midje.sweet
        ona.viewer.api))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}]
  (facts "about user-profile"
         "Should get correct url"
         (user-profile account) => :something
         (provided
          (make-url "profiles/" username) => url
          (parse-http :get url account) => :something))

  (facts "about projects"
         "Should get correct url"
         (projects account) => :something
         (provided
          (make-url "projects") => url
          (parse-http :get url account) => :something))

  (facts "about project-create"
         "Should associate data"
         (project-create account :name) => :something
         (provided
          (make-url "projects") => url
          (make-url "users/" username) => :owner-url
          (parse-http :post
                      url
                      account
                      {:form-params {:name :name
                                     :owner :owner-url}}) => :something))
  (facts "about datasets"
         "Should get correct url"
         (datasets account) => :something
         (provided
          (make-url "forms") => url
          (parse-http :get url account) => :something))

  (fact "about datasets-update"
    "Should get correct url"
    (dataset-update account :dataset-id :params) => :something
    (provided
      (make-url "forms/" :dataset-id) => url
      (parse-http :put url account {:form-params :params}) => :something)))
