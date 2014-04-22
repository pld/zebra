(ns ona.api.project_test
  (:use midje.sweet
        ona.api.user
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}
      params {:name "fake-name"
              :username "fake-username"
              :email "fake-email"
              :password "fake-password"
              :password2 "fake-password2"}]

  (facts "about user-profile"
         "Should get correct url"
         (profile account) => :something
         (provided
          (make-url "profiles/" username) => url
          (parse-http :get url account) => :something))
      
  (facts "about user registration"
         "can register a new user"
         (create params) => :someone
         (provided
          (make-url "profiles/" username) => url
          (parse-http :post url params) => :something)))
