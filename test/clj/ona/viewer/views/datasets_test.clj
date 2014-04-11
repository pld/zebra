(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.viewer.api :as api]))

(fact "about datasets"
       "Datasets view returns a list of datasets"
       (-> (datasets :fake-account) first last last) => :fake-title
       (provided
        (api/datasets :fake-account) => [{:title :fake-title}])

       "Dataset view returns data for dataset"
       (dataset :fake-account :dataset-id) => (contains (str :row))
       (provided
        (api/dataset-getdata :fake-account :dataset-id) => [:row]))
