(ns ona.api.project_test
  (:use midje.sweet
        ona.api.project
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}]

  (facts "about projects"
         "Should get correct url"
         (all account) => :something
         (provided
          (make-url "projects") => url
          (parse-http :get url account) => :something))

  (facts "about project-create"
         "Should associate data"
         (let [data {:url "a/b/c/id"}]
           (create account :data) => (merge data {:id "id"})
           (provided
            (make-url "projects") => url
            (parse-http :post
                        url
                        account
                        {:form-params :data}) => data))))
