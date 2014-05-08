(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.api.dataset :as api]))

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
