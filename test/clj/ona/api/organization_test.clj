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
                                    {:form-params :data}) => :something)))
