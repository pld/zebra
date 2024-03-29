(ns ona.viewer.views.datasets
  (:use [ona.viewer.helpers.projects :only [profile-with-projects]]
        [ona.viewer.helpers.tags :only [include-js js-tag]]
        [net.cgrand.enlive-html :only [emit*]])
  (:require [ona.api.dataset :as api]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]
            [ona.api.charts :as api-charts]
            [ona.viewer.helpers.sharing :as sharing]
            [ona.viewer.templates.base :as base]
            [ona.viewer.templates.datasets :as template]
            [ona.viewer.urls :as u]
            [cheshire.core :as cheshire]
            [ring.util.response :as response]
            [ona.utils.charts :as c]
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
  (concat
   [(js-tag "goog.require(\"ona.dataset\");")
    (js-tag "ona.dataset.init();")]
   (condp = context
     :map (let [data-var-name "data"
                map-content-id "map"]
            [(include-js "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js")
             [:link {:rel "stylesheet"
                     :href "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css"}]
             (js-tag "goog.require(\"ona.mapview\");")
             (js-tag (str "var " data-var-name "=" (as-geojson dataset) ";"))
             (js-tag (str "ona.mapview.leaflet(\""
                          map-content-id
                          "\",\""
                          data-var-name
                          "\");"))])
     nil)))

(defn- charts
  "Returns charts for charts context"
  [account dataset-id]
  (let [fields (api-charts/fields account dataset-id)
        field-names (keys (:fields fields))
        charts (for [field-name field-names]
                 (api-charts/chart account dataset-id (name field-name)))]
    charts))

(defn- update-hash
  "Build a hash for updates from existing data."
  [account dataset-id params]
  (let [defaults (select-keys (api/metadata account dataset-id)
                              [:description :owner :uuid :public :public_data])]
    (merge defaults params)))

(defn show
  "Show the data for a specific dataset."
  ([account owner project-id dataset-id]
     (show account owner project-id dataset-id nil))
  ([account owner project-id dataset-id context]
     (let [dataset (api/data account dataset-id)
           metadata (api/metadata account dataset-id)
           data-entry-link (api/online-data-entry-link account dataset-id)
           username (:username account)
           charts (if (= context :chart)
                    (map c/generate-bar (charts account dataset-id)))
           dataset-details {:dataset dataset
                            :metadata metadata
                            :data-entry-link data-entry-link
                            :charts charts}]
       (if context
         (emit* (template/view-for-context context dataset-details))
         (let [context :map]
           (base/base-template
            "/"
            account
            (:title metadata)
            (template/show owner
                           project-id
                           dataset-id
                           dataset-details
                           username
                           context)
            (js-for-context context dataset)))))))

(defn tags
  "View tags for a specific dataset"
  [account owner project-id dataset-id]
  (let [tags (api/tags account dataset-id)
        tag-form (template/new-tag-form owner project-id dataset-id)]
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
  (let [project (api-project/get-project account project-id)
        upload-path (u/dataset-new owner project-id)]
    (base/base-template
     (str "/" upload-path)
     account
     "New dataset"
     (template/new project)
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
  [account owner project-id dataset-id tags]
  (let [tags-to-add {:tags tags}
        added-tags (api/add-tags account dataset-id tags-to-add)]
    (response/redirect-after-post (u/dataset-tags owner
                                                  project-id
                                                  dataset-id))))

(defn get-file
  [file-path download-name format]
  (assoc
    (response/file-response file-path format)
    :headers
    {"Content-Type" (str "text/" format)
     "Content-disposition" (str "attachment;filename=" download-name)}))

(defn download
  "Download the data for a specific dataset as CSV."
  [account owner project-id dataset-id format-keyword]
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
     (template/metadata-form owner project-id dataset-id metadata))))

(defn update
  "Update metadata for a specific dataset"
  [account owner project-id dataset-id title description tags]
  (let [params (update-hash account
                            dataset-id
                            {:description description})]
    ;; TODO check that title is updated after onadata#359
    (api/update account dataset-id params))
  (api/add-tags account dataset-id {:tags tags})
  (response/redirect-after-post (u/dataset owner project-id dataset-id)))

(defn delete
  "Delete a dataset by ID."
  [account owner project-id dataset-id]
  (api/delete account dataset-id)
  (response/redirect (u/project-show owner project-id)))

(defn sharing
  "Sharing settings for a new dataset."
  [account owner project-id dataset-id]
  (let [metadata (api/metadata account dataset-id)]
    (base/base-template
     "/dataset"
     account
     "New dataset - Form settings"
     (template/sharing metadata owner project-id dataset-id))))

(defn sharing-update
  "Update sharing settings."
  [account owner params]
  (let [{:keys [dataset-id project-id]} params
        sharing-settings ((keyword sharing/settings) params)
        open-account? (= sharing-settings sharing/open-account)
        open-all? (= sharing-settings sharing/open-all)
        closed? (not= sharing-settings sharing/closed)
        params (update-hash account
                            dataset-id
                            {:public_data (str open-all?)
                             :downloadable (str closed?)})
        settings-url (u/dataset-settings owner project-id dataset-id)]
    (api/update account dataset-id params)
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
        all-users (api-user/all account)
        username (:username account)
        owner-profile (api-user/profile account owner)
        shared-users [(merge owner-profile
                             {:is-owner? (= username (:username owner-profile))})]]
    (base/base-template
      "/dataset"
      account
      (str "Sharing settings - " (:title metadata))
      (template/settings metadata
                         dataset-id
                         project-id
                         all-users
                         shared-users
                         username
                         owner))))

(defn settings-update
  "User share settings update"
  [account params]
  (let [dataset-id (:dataset-id params)
        project-id (:project-id params)
        owner (:username account)
        username (:username params)
        role (:role params)]
    (api/update-sharing account dataset-id username role)
    (response/redirect-after-post (u/dataset-metadata owner project-id dataset-id))))

(defn move-to-project
  "Move a dataset to a project"
  [account owner project-id dataset-id]
  (let [owner (:username account)]
    (api/move-to-project account dataset-id project-id)
    (response/redirect-after-post (u/project-show owner project-id))))
