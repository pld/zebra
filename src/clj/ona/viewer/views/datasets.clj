(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]]
        [ring.util.response :only [redirect-after-post]])
  (:require [ona.api.dataset :as api]
            [ona.viewer.views.templates :as t]
            [ring.util.response :as response]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)
        actions  [{:name "view data"}
                  {:name "view tags" :url "tags"}
                  {:name "download dataset" :url "download"}]]
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
  (t/base-template "/dataset" (:username account) "New dataset" (t/new-dataset-form)))

(defn create
  "Create a new dataset."
  [account params]
  (api/create account params)
  (redirect-after-post "/"))

(defn create-tags
  "Create tags for a specific dataset"
  [account params]
  (let [dataset-id (:dataset-id params)
        tags-to-add {:tags (:tags params)}
        added-tags (api/add-tags account dataset-id tags-to-add)]
    (tags account dataset-id)))

(defn write-file [dataset]
  (with-open [out-file (io/writer "download.csv" :append false)]
    (csv/write-csv out-file (for [dataitem dataset]
                              [(str dataitem)]))))
(defn assoc? [resp key values]
  (assoc resp key values))


(defn download
  "Show the data for a specific dataset."
  [account dataset-id format]
  (let [dataset (api/data account dataset-id)
        resp (response/file-response "download.csv")
        headers  {"Content-Type" " text/csv"
                  "Content-disposition" "attachment;filename=dataset.csv"}]
    (write-file dataset)
    (assoc? resp :headers headers)))
