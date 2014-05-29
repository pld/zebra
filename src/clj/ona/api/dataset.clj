(ns ona.api.dataset
  (:use [ona.api.io :only [make-url parse-http]]))

(defn- uploaded->file [uploaded-file]
  (let [{:keys [tempfile filename]} uploaded-file
        tempdir (com.google.common.io.Files/createTempDir)
        path (str (.getAbsolutePath tempdir) "/" filename)
        file (clojure.java.io/file path)]
    (.deleteOnExit file)
    (.deleteOnExit tempdir)
    (clojure.java.io/copy tempfile file)
    file))

(defn all
  "Return all the datasets for an account."
  [account]
  (let [url (make-url "forms")]
    (parse-http :get url account)))

(defn public
  "Return all public datasets for a specific user."
  [account username]
  (let [url (make-url "forms/" username)]
    (parse-http :get url account)))

(defn create
  "Create a new dataset from a file."
  [account uploaded-file]
  (let [xlsfile (uploaded->file uploaded-file)
        url (make-url "forms")]
    (parse-http :post url account
                {:multipart [{:name "xls_file"
                              :content xlsfile}]})))

(defn update
  "Set the metadata for a dataset."
  [account dataset-id params]
  (let [url (make-url "forms/" (:username account) "/" dataset-id)]
    (parse-http :put url account {:form-params params})))

(defn data
  "Return the data associated with a dataset."
  [account dataset-id]
  (let [url (make-url "data/" (:username account) "/" dataset-id)]
    (parse-http :get url account)))

(defn record
  "Retrieve a record from the dataset."
  [account dataset-id record-id]
  (let [url (make-url "data/" (:username account) "/" dataset-id "/" record-id)]
    (parse-http :get url account)))

(defn tags
  "Returns tags for a dataset"
  [account dataset-id]
  (let [url (make-url "forms/" (:username account) "/" dataset-id "/labels")]
    (parse-http :get url account)))

(defn add-tags
  "Add tags to a dataset"
  [account dataset-id tags]
    (let [url (make-url "forms/" (:username account) "/" dataset-id "/labels")]
    (parse-http :post url account {:form-params tags})))

(defn download
  "Download dataset in specified format."
  [account dataset-id]
  (let [filename (str dataset-id "." "csv")
        url (make-url "forms/" (:username account) "/" filename)]
    (parse-http :get url account nil filename)))

(defn metadata
  "Show dataset metadata."
  [account dataset-id]
  (let [url (make-url "forms/" (:username account) "/" dataset-id)]
    (parse-http :get url account)))

(defn online-data-entry-link
  "Return link to online data entry."
  [account dataset-id]
  (let [url (make-url "forms/" (:username account) "/" dataset-id "/enketo")]
    (:enketo_url (parse-http :get url account))))

(defn delete
  "Delete a dataset by ID."
  [account dataset-id]
  (let [url (make-url "forms/" dataset-id)]
    (parse-http :delete url account)))
