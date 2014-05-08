(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.dataset :as api]
            [ona.viewer.views.templates :as t]))

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
    (t/dashboard-items
      "Dataset"
      (:username account)
      (str "/dataset/" dataset-id)
      (for [dataitem dataset]
        {:item-id nil :item-name (str dataitem)}))))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account]
  (t/base-template "/dataset" (:username account) "New Dataset" (t/new-dataset-form)
                   [:script {:type "text/javascript"} "ona.core.init()"]))

(defn create
  "Create a new dataset."
  [account params])
