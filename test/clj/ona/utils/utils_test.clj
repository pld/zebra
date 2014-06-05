(ns ona.utils.utils_test
  (:use midje.sweet
        ona.viewer.utils.utils))

(fact "get-get-now interval should return interval between the time now and submission time"
      (get-now-interval "2014-6-4T10:18:23Z") => (contains (str "days")))
