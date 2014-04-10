(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.viewer.api :as api]))

(facts "about datasets"
       "Datasets view returns a list of datasets"
       (-> (datasets :fake-account) first last last) => :fake-title
       (provided
        (api/datasets :fake-account) => [{:title :fake-title}]))

(facts "about dataset"
       "Dataset view returns data for dataset"
       (dataset {:account :fake-account} :dataset-id) => (contains (str :row))
       (provided
        (api/dataset-getdata :fake-account :dataset-id) => [:row]))
