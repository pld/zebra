(ns ona.utils.time
  (:use [ona.utils.numeric :only [pluralize-number]])
  (:require [clj-time.core :as t]
            [clj-time.local :as l]))

(def seconds-in-minute 60)
(def minutes-in-hour 60)
(def hours-in-day 24)
(def days-in-month-average 30)
(def days-in-year 365)

(def seconds-in-hour (* minutes-in-hour seconds-in-minute))
(def seconds-in-day (* hours-in-day seconds-in-hour))
(def seconds-in-month-average (* days-in-month-average seconds-in-day))
(def seconds-in-year (* days-in-year seconds-in-day))

(def start-of-today (t/today-at 00 00 00))
(def end-of-today (t/today-at 23 59 59))

(defn interval->time-str
  "Convert an interval to an amount of time string."
  [interval]
  (let [interval-in-secs (t/in-seconds interval)]
    (apply pluralize-number
           (condp <= interval-in-secs
             seconds-in-year [(t/in-years interval) "year"]
             seconds-in-month-average [(t/in-months interval) "month"]
             seconds-in-day [(t/in-days interval) "day"]
             seconds-in-hour [(t/in-hours interval) "hour"]
             seconds-in-minute [(t/in-minutes interval) "minute"]
             [interval-in-secs "second"]))))

(defn date->days-ago-str
  "Get time interval in secs, mins, hours days or years for a given date time"
  [latest-submission-time]
  (if-let [time latest-submission-time]
    (let [interval (t/interval
                    (l/to-local-date-time time)
                    (l/local-now))]
      (interval->time-str interval))
    nil))

(defn time->interval-from-now
  "Gets interval from now for a given time"
  [time]
  (if time
    (t/in-seconds (t/interval (l/to-local-date-time time) (l/local-now)))))

(defn during-today?
  "Was the submission made during the current day?"
  [time-str]
  (t/within? (t/interval start-of-today
                         end-of-today)
             (l/to-local-date-time time-str)))

(defn get-no-submissions-today
  "Return the number of submission for this dataset made today."
  [dataset]
  (count (for [data dataset
               :when (during-today? (:_submission_time data))]
           data)))
