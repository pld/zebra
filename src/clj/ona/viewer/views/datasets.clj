(ns ona.viewer.views.datasets
  (:use [ring.util.response :only [redirect-after-post]]
        [ona.viewer.templates.helpers :only [include-js js-tag]])
  (:require [ona.api.dataset :as api]
            [ona.viewer.sharing :as sharing]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.forms :as forms]
            [ona.viewer.templates.datasets :as datasets]
            [cheshire.core :as cheshire]
            [ring.util.response :as response]))

(defn- as-geojson
  [dataset]
  (cheshire/generate-string
   {:type "FeatureCollection"
    :features (for [record dataset
                    :let [geo (:_geolocation record)]
                    :when (not (some nil? geo))]
                {:type "Feature"
                 :properties {:popup (str record)}
                 :geometry {:type "Point"
                            :coordinates (reverse (map read-string geo))}})}))

(defn- json-response
  "Return body wrappen in a JSON response."
  [body]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (cheshire/generate-string body)})

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)]
    datasets))

(defn show
  "Show the data for a specific dataset."
  ([account dataset-id]
   (show account dataset-id nil))
  ([account dataset-id context]
   (let [dataset (api/data account dataset-id)
        metadata (api/metadata account dataset-id)
        data-entry-link (api/online-data-entry-link account dataset-id)
        username (:username account)
        data-var-name "data"]

     (base/base-template
       "/"
       account
       (:title metadata)
       (datasets/show dataset-id metadata dataset data-entry-link username context)
       [(include-js "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js")
        [:link {:rel "stylesheet"
                :href "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css"}]
        (js-tag "goog.require(\"ona.mapview\");")
        (js-tag (str "var " data-var-name "=" (as-geojson dataset) ";"))
        (js-tag (str "ona.mapview.leaflet(\"map\",\"" data-var-name "\");"))]))))

(defn tags
  "View tags for a specific dataset"
  [account dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (forms/new-tag-form dataset-id)]
    (base/dashboard-items
      "Dataset tag"
      account
      (str "/dataset/" dataset-id)
      (for [tagitem tags]
        {:item-id nil :item-name (str tagitem)})
      tag-form)))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account]
  (base/base-template
   "/dataset"
   account
   "New dataset"
   (datasets/new-dataset)
   [(js-tag "goog.require(\"ona.upload\");")
    (js-tag "ona.upload.init(\"upload-button\", \"form\", \"/datasets\");")]))

(defn create
  "Create a new dataset."
  [account params]
  (let [response (api/create account params)]
    (if (and (contains? response :type) (= (:type response) "alert-error"))
      (:text response)
      (let [dataset-id (:formid response)
            preview-url (api/online-data-entry-link account dataset-id)]
        (json-response
         {:settings-url (str "/dataset/" dataset-id "/sharing")
          :preview-url preview-url
          :delete-url (str "/dataset/" dataset-id "/delete")})))))

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
  [account dataset-id format-keyword]
  (let [file-path (api/download account dataset-id)
        format (name format-keyword)
        metadata (api/metadata account dataset-id)
        download-name (str (:id_string metadata) "." format)]
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
                          :shared (if (:shared params) "True" "False")}]
    (api/update account dataset-id metadata-updates)
    (metadata account dataset-id)))

(defn delete
  "Delete a dataset by ID."
  [account id]
  (api/delete account id)
  (response/redirect "/dataset"))

(defn sharing
  "Sharing settings for a new dataset."
  [account dataset-id]
  (let [metadata (api/metadata account dataset-id)]
    (base/base-template
     "/dataset"
     account
     "New dataset - Form settings"
     (forms/sharing (:title metadata) dataset-id))))

(defn sharing-update
  "Update sharing settings."
  [account params]
  (let [dataset-id (:dataset-id params)
        sharing-settings ((keyword sharing/settings) params)
        update-data {:shared (if (= sharing-settings sharing/open-all)
                               "True"
                               "False")}]
    (api/update account dataset-id update-data)
    (redirect-after-post (str "/dataset/" dataset-id))))
