(ns ona.utils.time_test
  (:use midje.sweet
        ona.utils.time)
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]))

(let [now (l/local-now)
      days-ago-1 (t/minus now (t/days 1))
      days-ago-1-str (f/unparse (f/formatters :date-time)
                                days-ago-1)

      days-ago-4 (t/minus now (t/days 4))
      days-ago-4-str (f/unparse (f/formatters :date-time)
                                days-ago-4)
      year-ago (t/minus now (t/years 2))
      year-ago-str (f/unparse (f/formatters :date-time)
                              year-ago)]
  (facts "date->days-ago-str"
         "Should return interval between the time now and submission time"
         (date->days-ago-str days-ago-4-str) => (contains (str "days"))
         (date->days-ago-str year-ago-str) => (contains (str "years"))

         "Should not pluralize 1 time unit"
         (date->days-ago-str days-ago-1-str) => (contains (str "day"))))

(fact "get-no-submissions-today returns submissions for the day"
      (get-no-submissions-today [{:_xform_id_string "a submission"
                                  :_submission_time (l/local-now)}]) => 1)
