(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.dataset :as api]))

(defn datasets
  "List the datasets for this account."
  [account]
  (let [datasets (api/all account)]
        (for [dataset datasets]
          {:itemid (:formid dataset) :item-name (:title dataset)})))

(defn dataset
  "Show the data for a specific dataset."
  [account dataset-id]
  (let [dataset (api/data account dataset-id)]
    (base
      (for [dataitem dataset]
        [:p (str dataitem)]))))

(defn dataset-new [session])
