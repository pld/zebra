(ns ona.utils.utils_test
  (:use midje.sweet
        ona.viewer.utils.utils)
  (:require [clj-time.local :as l]))

(fact "get-get-now interval should return interval between the time now and submission time"
      (get-now-interval "2014-06-02T06:39:46.641Z") => (contains (str "days")))

(fact "get-no-submissions-today returns submissions for the day"
      (get-no-submissions-today [{:_xform_id_string "a submission"
                                  :_submission_time (l/local-now)}]) => 1)
