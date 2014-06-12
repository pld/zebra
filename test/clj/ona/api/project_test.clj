(ns ona.api.project_test
  (:use midje.sweet
        ona.api.project
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}
      data {:url "a/b/c/id"}
      parsed-data (merge data {:id "id"})]

  (facts "about projects"
         "Should get correct url"
         (all account) => :something
         (provided
          (make-url "projects") => url
          (parse-http :get url account) => :something))

  (facts "about project-create"
         "Should associate data"
         (create account :data username) => parsed-data
         (provided
          (make-url "projects/" username) => url
          (parse-http :post
                      url
                      account
                      {:form-params :data}) => data)

         "Should throw an exception if special __all__ error key returned"
         (let [error :error]
           (create account :data username) => (throws clojure.lang.ExceptionInfo)
           (provided
            (make-url "projects/" username) => url
            (parse-http :post
                        url
                        account
                        {:form-params :data}) => {:__all__ error})))

  (facts "about get-project"
         "Should find project for id"
         (get-project account :id) => parsed-data
         (provided
          (make-url "projects/" username "/" :id) => url
          (parse-http :get
                      url
                      account) => data))

    (facts "about get-forms"
         "Should find forms for id"
         (get-forms account :id) => data
         (provided
          (make-url "projects/" username "/" :id "/forms") => url
          (parse-http :get
                      url
                      account) => data)))
