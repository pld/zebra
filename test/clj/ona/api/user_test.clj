(ns ona.api.project_test
  (:use midje.sweet
        ona.api.user
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}]

  (facts "about user-profile"
         "Should get correct url"
         (profile account) => :something
         (provided
          (make-url "profiles/" username) => url
          (parse-http :get url account) => :something)))
