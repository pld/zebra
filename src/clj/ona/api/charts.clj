(ns ona.api.charts
  (:use [ona.api.io :only [make-url parse-http]]))

(defn- ext
  ([dataset-id]
   (str dataset-id ".json"))
  ([dataset-id field-name]
   (str dataset-id ".json?field_name=" field-name)))

(defn fields
  "Get list of chart fields for a specific dataset"
  [account dataset-id]
  (let [url (make-url "charts" (ext dataset-id))]
        (parse-http :get url account)))

(defn chart
  "Get chart for a specifc field in a dataset"
  [account dataset-id field-name]
  (let [url (make-url "charts" (ext dataset-id field-name))]
    (parse-http :get url account)))