(ns ona.api.dataset_test
  (:use midje.sweet
        ona.api.dataset
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}]

  (facts "about datasets"
         "Should get correct url"
         (all account) => :something
         (provided
           (make-url "forms") => url
           (parse-http :get url account) => :something))

  (fact "about datasets-update"
        "Should get correct url"
        (update account :dataset-id :params) => :something
        (provided
          (make-url "forms/" :dataset-id) => url
          (parse-http :put url account {:form-params :params}) => :something))

  (facts "about dataset-getdata"
         (data account :dataset-id) => :something
         (provided
           (make-url "data/" username "/" :dataset-id) => url
           (parse-http :get url account) => :something))

  (facts "about dataset-getsubmission"
         (submission account :dataset-id :submission-id) => :something
         (provided
           (make-url "data/" username "/" :dataset-id  "/" :submission-id) => url
           (parse-http :get url account) => :something))

  (facts "about  dataset-get-tags"
         (tags account :dataset-id) => :something
         (provided
           (make-url "forms/" username "/" :dataset-id "/" "labels" ) => url
           (parse-http :get url account) => :something
           )))
