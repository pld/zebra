(ns ona.viewer.api-test
  (:use midje.sweet
        ona.viewer.api))

(let [url :fake-url
             username :fake-username
             password :fake-password
             account {:username username :password password}]
  (facts "about user-profile"
         "Should parse response body"
         (user-profile account) => :something
         (provided
          (make-url (str "profiles/" username)) => url
          (parse-http url account) => :something))

  (facts "about projects"
         "Should parse response body"
         (projects account) => :something
         (provided
          (make-url "projects") => url
          (parse-http url account) => :something)))
