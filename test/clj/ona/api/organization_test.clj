(ns ona.api.organization_test
  (:use midje.sweet
        ona.api.organization
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}]

  (facts "about organizations"
         "should get correct url"
         (all account) => :something
         (provided
           (make-url "orgs") => url
           (parse-http :get url account) => :something))

  (facts "about organization-create"
         "Should associate data"
         (create account :data) => :something
         (provided
           (make-url "orgs") => url
           (parse-http :post
                       url
                       account
                       {:form-params :data}) => :something))

  (facts "about teams"
         "should get correct url"
         (teams account :fake-orgname) => :something
         (provided
           (make-url "teams" :fake-orgname) => url
           (parse-http :get url account) => :something))

  (facts "about team-info"
         "should get correct url"
         (team-info account :fake-orgname :fake-team-id) => :something
         (provided
           (make-url "teams" :fake-orgname :fake-team-id) => url
           (parse-http :get url account) => :something))

  (facts "about team-members"
         "should get correct url"
         (team-members account :fake-orgname :fake-team-id) => :something
         (provided
           (make-url "teams" :fake-orgname :fake-team-id "members") => url
           (parse-http :get url account) => :something))

  (facts "about create-team"
         (create-team  account :params) => :something
         (provided
           (make-url "teams") => url
           (parse-http :post url account {:form-params :params}) => :something))

  (facts "about add-team-member"
         (add-team-member  account :fake-orgname :fake-team-id :user) => :something
         (provided
           (make-url "teams" :fake-orgname :fake-team-id "members") => url
           (parse-http :post url account {:form-params :user}) => :something))

  (facts "about members"
         "should get correct url"
         (members account :fake-orgname) => :something
         (provided
           (make-url "orgs" :fake-orgname "members") => url
           (parse-http :get url account) => :something))

  (facts "about add-member"
         (add-member account :orgname :member) => :something
         (provided
           (make-url "orgs" :orgname "members") => url
           (parse-http :post
                       url
                       account
                       {:form-params {:username :member}}) => :something)))
