(ns ona.api.dataset_test
  (:use midje.sweet
        ona.api.dataset
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      username :fake-username
      password :fake-password
      account {:username username :password password}
      params {:description :description
              :owner :owner
              :public :public
              :public_data :public_data
              :uuid :uuid}]
  (facts "about datasets"
         "Should get correct url"
         (all account) => :something
         (provided
           (make-url "forms") => url
           (parse-http :get url account) => :something))

  (fact "about datasets-update"
        "Should get correct url"
        (update account :dataset-id params) => :something
        (provided
          (make-url "forms" :dataset-id) => url
          (parse-http :put url account {:form-params params}) => :something))

  (fact "about dataset metadata"
        "should get dataset metadata"
        (metadata account :dataset-id) => :fake-metadata
        (provided
          (make-url "forms" :dataset-id) => url
          (parse-http :get url account) => :fake-metadata))

  (facts "about dataset-getdata"
         (data account :dataset-id) => :something
         (provided
           (make-url "data" :dataset-id) => url
           (parse-http :get url account) => :something))

  (facts "about dataset-getrecord"
         (record account :dataset-id :record-id) => :something
         (provided
           (make-url "data" :dataset-id :record-id) => url
           (parse-http :get url account) => :something))

  (facts "about dataset-get-tags"
         (tags account :dataset-id) => :something
         (provided
           (make-url "forms" :dataset-id "labels") => url
           (parse-http :get url account) => :something))

  (facts "about dataset-add-tag"
         (add-tags  account :dataset-id :tags) => :something
         (provided
           (make-url "forms" :dataset-id "labels") => url
           (parse-http :post url account {:form-params :tags}) => :something))

  (facts "about dataset download"
         (let [filename (str :dataset-id "." "csv")]
           (download account :dataset-id) => :fake-file
           (provided
             (make-url "forms" filename) => url
             (parse-http :get url account nil filename) => :fake-file)))

  (facts "about online-data-entry-link"
         (online-data-entry-link account :dataset-id) => :response
         (provided
          (make-url "forms" :dataset-id "enketo") => url
          (parse-http :get url account) => {:enketo_url :response}))

  (facts "about dataset delete"
         (delete account :dataset-id) => :response
         (provided
          (make-url "forms" :dataset-id) => url
          (parse-http :delete url account) => :response))

  (facts "about create dataset"
         (create account :file) => :response
         (provided
          (#'ona.api.dataset/uploaded->file :file) => :xlsfile
          (make-url "forms") => url
          (parse-http :post
                      url
                      account
                      {:multipart [{:name "xls_file"
                                    :content :xlsfile}]}) => :response))

  (facts "about move dataset to folder"
         (move-to-project account 1 :project-id) => :form
         (provided
           (make-url "projects" :project-id "forms") => url
           (parse-http :post
                       url
                       account
                       {:form-params {:formid 1}}
                       ) => :form ))

  (facts "about update-sharing for dataset"
         "Should return result of parse-http"
         (let [username :fake-username
               role :fake-role
               data {:username username :role role}]
           (update-sharing account :dataset-id username role) => :sharing-updated
           (provided
             (make-url "forms" :dataset-id "share") => url
             (parse-http :post url account {:form-params data}) => :sharing-updated ))))
