(ns ona.viewer.views.datasets
  (:use [ring.util.response :only [redirect-after-post]]
        [ona.viewer.helpers.tags :only [include-js js-tag]])
  (:require [ona.api.dataset :as api]
            [ona.api.project :as api-project]
            [ona.viewer.sharing :as sharing]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.forms :as forms]
            [ona.viewer.templates.datasets :as datasets]
            [ona.viewer.urls :as u]
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

(defn- js-for-context
  "Return the JavaScript appropriate for the context."
  [context dataset]
  (condp = context
    :map (let [data-var-name "data"]
           [(include-js "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js")
            [:link {:rel "stylesheet"
                    :href "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css"}]
            (js-tag "goog.require(\"ona.mapview\");")
            (js-tag (str "var " data-var-name "=" (as-geojson dataset) ";"))
            (js-tag (str "ona.mapview.leaflet(\"map\",\"" data-var-name "\");"))])
    nil))

(defn all
  "Return all the datasets for this account."
  [account]
  (let [datasets (api/all account)]
    datasets))

(defn show
  "Show the data for a specific dataset."
  ([account dataset-id]
   (show account dataset-id :map))
  ([account dataset-id context]
   (let [dataset (api/data account dataset-id)
        metadata (api/metadata account dataset-id)
        data-entry-link (api/online-data-entry-link account dataset-id)
        username (:username account)]

     (base/base-template
       "/"
       account
       (:title metadata)
       (datasets/show dataset-id metadata dataset data-entry-link username context)
       (js-for-context context dataset)))))

(defn tags
  "View tags for a specific dataset"
  [account dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (forms/new-tag-form dataset-id)]
    (base/dashboard-items
      "Dataset tag"
      account
      (u/dataset dataset-id)
      (for [tagitem tags]
        {:id nil :name (str tagitem)})
      tag-form)))

(defn new-dataset
  "Render a page for creating a new dataset."
  ([account]
     (new-dataset account nil nil))
  ([account owner project-id]
     (let [project (if owner
                     (api-project/get-project account owner project-id)
                     {})
           upload-path (if owner
                         (u/project-new-dataset project-id owner)
                         "dataset")]
       (base/base-template
        (str "/" upload-path)
        account
        "New dataset"
        (datasets/new-dataset project)
        [(js-tag "goog.require(\"ona.upload\");")
         (js-tag (str "ona.upload.init(\"upload-button\", \"form\", \""
                      upload-path
                      "\");"))]))))

(defn create
  "Create a new dataset."
  ([account file]
     (create account file nil))
  ([account file project-id]
     (let [response (api/create account file project-id)]
       (if (and (contains? response :type) (= (:type response) "alert-error"))
         (:text response)
         (let [dataset-id (:formid response)
               preview-url (api/online-data-entry-link account dataset-id)]
           (json-response
            {:settings-url (u/dataset-sharing dataset-id)
             :preview-url preview-url
             :delete-url (u/dataset-delete dataset-id)}))))))

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
        metadata-form (forms/metadata-form dataset-id metadata)]
    (base/dashboard-items
      "Dataset metadata"
      (:username account)
      (u/dataset dataset-id)
      [{:name metadata}]
      metadata-form)))

(defn update
  "Update metadata for a specific dataset"
  [account params]
  (let [dataset-id (:dataset-id params)
        metadata-updates {:description (:description params)
                          :title (:title params)}
        tags {:tags (:tags params)}]
    (api/update account dataset-id metadata-updates)
    (api/add-tags account dataset-id tags)
    (redirect-after-post (u/dataset dataset-id))))

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
     (forms/sharing metadata dataset-id))))

(defn sharing-update
  "Update sharing settings."
  [account params]
  (let [dataset-id (:dataset-id params)
        sharing-settings ((keyword sharing/settings) params)
        update-data {:shared (if (= sharing-settings sharing/open-all)
                               "True"
                               "False")}]
    (api/update account dataset-id update-data)
    (redirect-after-post (u/dataset-metadata dataset-id))))
