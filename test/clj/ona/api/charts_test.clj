(ns ona.api.charts-test
  (:use midje.sweet
        ona.api.charts
        [ona.api.io :only [make-url parse-http]]))

(let [url :fake-url
      account :fake-account
      dataset-id :fake-dataset-id]

    (facts "about fields"
       "Should get correct url for chart fields"
       (fields account dataset-id) => :some-fields
       (provided
         (make-url "charts" (str dataset-id ".json")) => url
         (parse-http :get url account) => :some-fields))

    (facts "about chart"
           "Should get correct url for chart"
           (chart account dataset-id :field-name) => :some-chart
           (provided
             (make-url "charts" (str dataset-id ".json?field_name=" :field-name)) => url
             (parse-http :get url account) => :some-chart)))
