(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.api.dataset :as api]
            [ona.api.project :as api-project]
            [ring.util.response :as response]))

(facts "about datasets show"
       "Should returns data for dataset"
      (show :fake-account :dataset-id) => (contains "Some title")
      (provided
        (api/data :fake-account :dataset-id) => [:row]
        (api/metadata :fake-account :dataset-id) => {:title "Some title"
                                                     :last_submission_time "2014-6-3T20:18:23Z"}))

(facts "about datasets new"
      "Should return content for creating a dataset"
      (new-dataset :fake-account) =not=> nil

      "Should take project id for project specific dataset"
      (let [project-name "the project name"]
        (new-dataset :fake-account :owner :project-id) => (contains project-name)
        (provided
         (api-project/get-project :fake-account :owner :project-id) => {:name project-name})))

(fact "about dataset tags"
      "Tags returns all tags for a specific dataset"
      (tags :fake-account :dataset-id) => (contains (str :fake-tags))
      (provided
        (api/tags :fake-account :dataset-id) => [:fake-tags])

      "Create tags creates tags for a specific dataset"
      (let [tags {:tags "tag1, tag2"}
            params (merge {:dataset-id :dataset-id} tags)]
        (create-tags :fake-account params) => :something
        (provided
          (api/add-tags :fake-account :dataset-id tags) => :new-tags
          (tags :fake-account :dataset-id) => :something)))

(fact "about dataset download"
      "Downloads dataset with specified format"
      (let [dataset-id :dataset-id
            format (str "csv")
            id-string :id-string
            download-name (str id-string "." format)]
        (download :fake-account dataset-id format) => :fake-download
        (provided
          (api/download :fake-account dataset-id) => :file-path
          (get-file :file-path download-name format) => :fake-download
          (api/metadata :fake-account dataset-id) => {:id_string id-string})))

(fact "about dataset metadata"
      "Should show metadata for a specific dataset"
     (metadata :fake-account :dataset-id) => (contains "some data")

     (provided
       (api/metadata :fake-account :dataset-id) => "some data")

      "Should update metadata for a specific dataset"
      (let [metadata-updates {:description "test description" :title "test title"}
            dataset-id 1
            params (merge {:dataset-id dataset-id} metadata-updates)]
        (update :fake-account params) => (contains {:status 303})
        (provided
          (api/update :fake-account dataset-id metadata-updates) => :updated-metadata)))

(fact "about dataset delete"
      "Should delete a dataset"
      (:status (delete :fake-account :dataset-id)) => 302
      (provided
       (api/delete :fake-account :dataset-id) => nil))

(fact "about dataset/create"
      "Should return :text value on error"
      (create :fake-account :params) => :response
      (provided
       (api/create :fake-account :params nil nil) => {:type "alert-error"
                                                      :text :response})

      "Should return link to preview URL on success"
      (cheshire.core/parse-string (:body (create :fake-account :params)) true) =>
      {:preview-url "preview-url"
       :settings-url (str "/dataset/" :dataset-id "/sharing")
       :delete-url (str "/dataset/"
                        :dataset-id
                        "/delete")}
      (provided
       (api/create :fake-account :params nil nil) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :dataset-id) => :preview-url)

      "Should upload to project if project-id passed"
      (cheshire.core/parse-string (:body (create :fake-account
                                                 :params
                                                 :owner
                                                 :project-id)) true) =>
      {:preview-url "preview-url"
       :settings-url (str "/dataset/" :dataset-id "/sharing")
       :delete-url (str "/dataset/"
                        :dataset-id
                        "/delete")}
      (provided
       (api/create :fake-account :params :owner :project-id) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :dataset-id) => :preview-url))
