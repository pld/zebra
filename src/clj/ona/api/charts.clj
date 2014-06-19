(ns ona.api.charts
  (:use [ona.api.io :only [make-url parse-http]]))

(defn fields
  "Get list of chart fields for a specific dataset"
  [account dataset-id]
  (let [url (make-url "charts" dataset-id)]
        (parse-http :get url account)))