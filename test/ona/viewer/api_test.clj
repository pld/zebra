(ns ona.viewer.api-test
  (:use midje.sweet
        ona.viewer.api))

(facts "about user-profile"
       "Should parse response body"
       (let [url :fake-url
             username :fake-username
             password :fake-password
             account {:username username :password password}]
         (user-profile account) => :something
         (provided
          (make-url (str "profiles/" username)) => url
          (parse-http url {:basic-auth [username password]}) => :something)))
