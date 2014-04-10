(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets)
  (:require [ona.viewer.api :as api]))

(facts "about datasets"
       "Datasets view returns a list of datasets"
       (datasets :fake-account) => (contains [[:a ":fake-dataset"]])
       (provided
        (api/datasets :fake-account) => [:fake-dataset]))

(facts "about datasets"
		"Dataset view returns data for dataset"
		))