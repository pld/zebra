(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.dataset :as api]
            [ona.viewer.views.templates :as t]))

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)
        actions  [{:name "view data" :url ""}{:name "view tags" :url "tags"}]]
    (for [dataset datasets]
      {:item-id (:formid dataset) :item-name (:title dataset) :actions actions})))

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

(defn tags
  "View tags for a specific dataset"
  [account dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (t/new-tag-form dataset-id)]
    (t/dashboard-items
      "Dataset tag"
      (:username account)
      (str "/dataset/" dataset-id)
      (for [tagitem tags]
        {:item-id nil :item-name (str tagitem)})
      tag-form)))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account]
  (t/base-template "/dataset" (:username account) "New Dataset" (t/new-dataset-form)
                   [:script {:type "text/javascript"} "ona.core.init()"]))

(defn create
  "Create a new dataset."
  [account params])
