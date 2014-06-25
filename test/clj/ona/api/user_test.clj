(ns ona.api.user_test
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
              :password2 "fake-password2"}
      update-params {:name "fake-name"
                     :email "fake-email"
                     :city "fake-city"
                     :country "fakecountry"
                     :org "fake-org"
                     :website "fake-website"}
      data {:form-params params}
      updated-data {:form-params update-params}]

  (facts "about user-profile"
         "Should get correct url"
         (profile account) => :something
         (provided
          (make-url "profiles" username) => url
          (parse-http :get url account) => :something))

  (facts "about user registration"
         "can register a new user"
         (create params) => :someone
         (provided
          (make-url "profiles") => url
          (parse-http :post url nil data) => :someone))

  (facts "about users list"
         (all account) => :userlist
         (provided
           (make-url "users") => url
           (parse-http :get url account) => :userlist))

  (facts "about user update profile"
         (update account update-params) => :updated-profile
         (provided
           (make-url "profiles" username) => url
           (parse-http :patch url account updated-data) => :updated-profile)))

