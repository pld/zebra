(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.api.dataset :as api]
            [ring.util.response :as response]))

(fact "about datasets"
      "Datasets all returns a list of datasets"
      (-> (all :fake-account) first second last) => :fake-title
      (provided
        (api/all :fake-account) => [{:title :fake-title}])

      "Dataset show returns data for dataset"
      (show :fake-account :dataset-id) => (contains (str :row))
      (provided
        (api/data :fake-account :dataset-id) => [:row])

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
      (let [headers {"Content-Type" " text/csv"
                  "Content-disposition" "attachment;filename=dataset.csv"}]
      (download :fake-account :dataset-id :format) => :fake-download
      (provided
        (api/data :fake-account :dataset-id) => [:row]
        (write-file [:row]) => :fake-download
        (response/file-response "download.csv") => :fake-response
        (assoc? :fake-response :headers headers) => :fake-download)))
