(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.api.dataset :as api]
            [ring.util.response :as response]))

(fact "about datasets"
      "Datasets all returns a list of datasets"
      (-> (all :fake-account) first first last) => :fake-title
      (provided
        (api/all :fake-account) => [{:title :fake-title}])

      "Dataset show returns data for dataset"
      (show :fake-account :dataset-id) => (contains "Some title")
      (provided
        (api/data :fake-account :dataset-id) => [:row]
        (api/metadata :fake-account :dataset-id) => {:title "Some title"})

      "Dataset new returns content for creating a dataset"
      (new-dataset :fake-account) =not=> nil)

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
            download-name (str dataset-id "." format)]
        (download :fake-account dataset-id format) => :fake-download
        (provided
          (api/download :fake-account dataset-id) => :file-path
          (get-file :file-path download-name format) => :fake-download)))

(fact "about dataset metadata"
      "Should show metadata for a specific dataset"
     (metadata :fake-account :dataset-id) => (contains "some data")

     (provided
       (api/metadata :fake-account :dataset-id) => "some data")

      "Should update metadata for a specific dataset"
      (let [metadata-updates {:description "test description" :shared "True"}
            params (merge {:dataset-id :dataset-id} metadata-updates)]
        (update :fake-account params) => :something
        (provided
          (api/update :fake-account :dataset-id metadata-updates) => :updated-metadata
          (metadata :fake-account :dataset-id) => :something)))

(fact "about dataset delete"
      "Should delete a dataset"
      (:status (delete :fake-account :dataset-id)) => 302
      (provided
       (api/delete :fake-account :dataset-id) => nil))

(fact "about dataset/create"
      "Should return :text value on error"
      (create :fake-account :params) => :response
      (provided
       (api/create :fake-account :params) => {:type "alert-error"
                                              :text :response})

      "Should return link to preview URL on success"
      (create :fake-account :params) => {:preview-url :preview-url
                                         :settings-url (str "/dataset/" :dataset-id)
                                         :delete-url (str "/dataset/"
                                                          :dataset-id
                                                          "/delete")}
      (provided
       (api/create :fake-account :params) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :dataset-id) => :preview-url))
