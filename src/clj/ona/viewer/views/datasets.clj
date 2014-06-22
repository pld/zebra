(ns ona.viewer.views.datasets
  (:use [ona.viewer.helpers.projects :only [profile-with-projects]]
        [ona.viewer.helpers.tags :only [include-js js-tag]])
  (:require [ona.api.dataset :as api]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]
            [ona.viewer.helpers.sharing :as sharing]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.forms :as forms]
            [ona.viewer.templates.datasets :as datasets]
            [ona.viewer.urls :as u]
            [cheshire.core :as cheshire]
            [ring.util.response :as response]
            [ona.utils.string :as s]))

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

(defn show
  "Show the data for a specific dataset."
  ([account owner project-id dataset-id]
   (show account owner project-id dataset-id :map))
  ([account owner project-id dataset-id context]
   (let [dataset (api/data account dataset-id)
        metadata (api/metadata account dataset-id)
        data-entry-link (api/online-data-entry-link account dataset-id)
        username (:username account)]
     (base/base-template
       "/"
       account
       (:title metadata)
       (datasets/show owner
                      project-id
                      dataset-id
                      metadata
                      dataset
                      data-entry-link
                      username
                      context)
       (js-for-context context dataset)))))

(defn tags
  "View tags for a specific dataset"
  [account owner project-id dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (forms/new-tag-form owner project-id dataset-id)]
    (base/dashboard-items
      "Dataset tag"
      account
      (u/dataset owner project-id dataset-id)
      (for [tagitem tags]
        {:id nil :name (str tagitem)})
      tag-form)))

(defn new-dataset
  "Render a page for creating a new dataset."
  [account owner project-id]
  (let [project (api-project/get-project account owner project-id)
        upload-path (u/dataset-new owner project-id)]
    (base/base-template
     (str "/" upload-path)
     account
     "New dataset"
     (datasets/new-dataset project)
     [(js-tag "goog.require(\"ona.upload\");")
      (js-tag (str "ona.upload.init(\"upload-button\", \"form\", \""
                   upload-path
                   "\");"))])))

(defn create
  "Create a new dataset."
  [account owner project-id file]
  (let [response (api/create account file owner project-id)]
    (if (and (contains? response :type) (= (:type response) "alert-error"))
      (:text response)
      (let [dataset-id (:formid response)
            preview-url (api/online-data-entry-link account dataset-id)]
        (json-response
         {:settings-url (u/dataset-sharing owner project-id dataset-id)
          :preview-url preview-url
          :delete-url (u/dataset-delete owner project-id dataset-id)})))))

(defn create-tags
  "Create tags for a specific dataset"
  [account dataset-id project-id tags]
  (let [tags-to-add {:tags tags}
        added-tags (api/add-tags account dataset-id tags-to-add)]
    (response/redirect-after-post (u/dataset-tags dataset-id
                                                  project-id))))

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
  [account owner project-id dataset-id]
  (let [metadata (api/metadata account dataset-id)]
    (base/base-template
     (u/dataset-metadata owner project-id dataset-id)
     account
     "Dataset metadata"
     (forms/metadata-form owner project-id dataset-id metadata))))

(defn update
  "Update metadata for a specific dataset"
  [account owner project-id dataset-id title description tags]
  (let [defaults (select-keys (api/metadata account dataset-id)
                              [:owner :uuid :public :public_data])]
    ;; TODO check that title gets update after onadata#359
    (api/update account dataset-id {:description description}))
  (api/add-tags account dataset-id {:tags tags})
  (response/redirect-after-post (u/dataset owner project-id dataset-id)))

(defn delete
  "Delete a dataset by ID."
  [account owner project-id dataset-id]
  (api/delete account dataset-id)
  (response/redirect "/"))

(defn sharing
  "Sharing settings for a new dataset."
  [account owner project-id dataset-id]
  (let [metadata (api/metadata account dataset-id)]
    (base/base-template
     "/dataset"
     account
     "New dataset - Form settings"
     (forms/sharing metadata owner project-id dataset-id))))

(defn sharing-update
  "Update sharing settings."
  [account owner params]
  (let [{:keys [dataset-id project-id]} params
        sharing-settings ((keyword sharing/settings) params)
        open-account? (= sharing-settings sharing/open-account)
        open-all? (= sharing-settings sharing/open-all)
        update-data {:shared (if open-all?
                               "True"
                               "False")}
        settings-url (u/dataset-settings owner project-id dataset-id)]
    (api/update account dataset-id project-id update-data)
    (cond
     open-all? (response/redirect-after-post settings-url)
     open-account? (response/redirect-after-post settings-url)
      :else (response/redirect-after-post (u/dataset-metadata owner
                                                              project-id
                                                              dataset-id)))))

(defn settings
  "Project settings page."
  [account owner project-id dataset-id]
  (let [metadata (api/metadata account dataset-id)
        users (api-user/all account)
        profile (api-user/profile account owner)]
    (base/base-template
      "/dataset"
      account
      (str "Sharing settings - " (:title metadata))
      (forms/settings metadata dataset-id project-id users profile))))

(defn settings-update
  "User share settings update"
  [account params]
  (let [dataset-id (:dataset-id params)
        project-id (:project-id params)
        owner (:username account)
        username (:username params)
        role (:role params)]
    (api/update-sharing account dataset-id owner username role)
    (response/redirect-after-post (u/dataset-metadata owner project-id dataset-id))))

(defn move-to-project
  "Move a dataset to a project"
  [account owner project-id dataset-id]
  (let [owner (:username account)]
    (api/move-to-project account dataset-id project-id owner)
    (response/redirect-after-post (u/project-show owner project-id))))
