(ns ona.api.project_test
  (:use midje.sweet
        ona.api.project
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}
      data {:url "a/b/c/id"}
      data-with-owner (merge data
                             {:owner url})
      parsed-data (merge data {:id "id"})]

  (facts "about projects all"
         "Should get correct url"
         (all account) => :response
         (provided
          (make-url "projects") => url
          (parse-http :get url account nil) => :response)

         "Should pass owner as a query parameter"
         (all account username) => :response
         (provided
          (make-url "projects") => url
          (parse-http :get url account {:query-params {:owner username}}) => :response))

  (facts "about project-create"
         "Should associate data"
         (create account data username) => parsed-data
         (provided
          (make-url "users" username) => url
          (make-url "projects") => url
          (parse-http :post
                      url
                      account
                      {:form-params data-with-owner}) => data)

         "Should throw an exception if special __all__ error key returned"
         (let [error :error]
           (create account data username) => (throws clojure.lang.ExceptionInfo)
           (provided
            (make-url "users" username) => url
            (make-url "projects") => url
            (parse-http :post
                        url
                        account
                        {:form-params data-with-owner}) => {:__all__ error})))

  (facts "about get-project"
         "Should find project for id"
         (get-project account :id) => parsed-data
         (provided
          (make-url "projects" :id) => url
          (parse-http :get
                      url
                      account) => data))

    (facts "about get-forms"
         "Should find forms for id"
         (get-forms account :id) => data
         (provided
          (make-url "projects" :id "forms") => url
          (parse-http :get
                      url
                      account) => data)))
