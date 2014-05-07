(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]]
        [ona.viewer.views.templates :only [dashboard-items]])
  (:require [ona.api.dataset :as api]))

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)]
    (for [dataset datasets]
      {:item-id (:formid dataset) :item-name (:title dataset)})))

(defn show
  "Show the data for a specific dataset."
  [account dataset-id]
  (let [dataset (api/data account dataset-id)]
    (dashboard-items
      "Dataset"
      (:username account)
      "dataset/"
      (for [dataitem dataset]
        {:item-id nil :item-name (str dataitem)}))))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account]
  ())
