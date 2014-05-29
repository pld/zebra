(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ring.util.response :only [redirect-after-post]])
  (:require [ona.api.dataset :as api]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.forms :as forms]
            [ona.viewer.templates.datasets :as datasets]
            [ring.util.response :as response]))

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)]
    datasets))

(defn show
  "Show the data for a specific dataset."
  [account dataset-id]
  (let [dataset (api/data account dataset-id)
        metadata (api/metadata account dataset-id)]
    (base/base-template
      "/"
      (:username account)
      (:title metadata)
      (datasets/show metadata dataset)
      )))

(defn tags
  "View tags for a specific dataset"
  [account dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (forms/new-tag-form dataset-id)]
    (base/dashboard-items
      "Dataset tag"
      (:username account)
      (str "/dataset/" dataset-id)
      (for [tagitem tags]
        {:item-id nil :item-name (str tagitem)})
      tag-form)))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account]
  (base/base-template
   "/dataset"
   (:username account)
   "New dataset"
   (datasets/new-dataset)
   nil
   "ona.upload.init(\"upload-button\", \"form\", \"/datasets\");"))

(defn create
  "Create a new dataset."
  [account params]
  (let [response (api/create account params)]
;    (throw (Exception. (str response)))
    )
  (redirect-after-post "/"))

(defn create-tags
  "Create tags for a specific dataset"
  [account params]
  (let [dataset-id (:dataset-id params)
        tags-to-add {:tags (:tags params)}
        added-tags (api/add-tags account dataset-id tags-to-add)]
    (tags account dataset-id)))

(defn get-file
  [file-path download-name format]
  (assoc
    (response/file-response file-path format)
    :headers
    {"Content-Type" (str "text/" format)
     "Content-disposition" (str "attachment;filename=" download-name)}))

(defn download
  "Download the data for a specific dataset as CSV."
  [account dataset-id format]
  (let [file-path (api/download account dataset-id)
        format "csv"
        download-name (str dataset-id "." format)]
    (get-file file-path download-name format)))

(defn metadata
  "View metadata for specific form"
  [account dataset-id]
  (let [metadata (api/metadata account dataset-id)
        metadata-form (forms/metadata-form dataset-id)]
    (base/dashboard-items
      "Dataset metadata"
      (:username account)
      (str "/dataset/" dataset-id)
      [{:item-name metadata}]
      metadata-form)))

(defn update
  "Update metadata for a specific dataset"
  [account params]
  (let [dataset-id (:dataset-id params)
        metadata-updates {:description (:description params)
                          :shared (if (:shared params) "True" "False")}
        updated-metadata (api/update account dataset-id metadata-updates)]
    (metadata account dataset-id)))
