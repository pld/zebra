(ns ona.api.charts
  (:use [ona.api.io :only [make-url parse-http]]))

(defn fields
  "Get list of chart fields for a specific dataset"
  [account dataset-id]
  (let [url (make-url "charts" (str dataset-id ".json"))]
        (parse-http :get url account)))

(defn chart
  "Get chart for a specifc field in a dataset"
  [account dataset-id field-name]
  (let [url (make-url "charts" (str dataset-id ".json?field_name=" field-name))]
    (parse-http :get url account)))
