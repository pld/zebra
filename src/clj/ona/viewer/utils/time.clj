(ns ona.viewer.utils.time
  (:use [inflections.core :only [plural]])
  (:require [clj-time.core :as t]
            [clj-time.local :as l]))

(def seconds-in-minute 60)
(def minutes-in-hour 60)
(def hours-in-day 24)
(def days-in-year 365)

(defn pluralize-number
  "Create an appropriately pluralized string prefix by number."
  [number kind]
  (str number
       " "
       (if (= 1 number) kind (plural kind))))

(defn interval->time-str
  "Convert an interval to an amount of time string."
  [interval]
  (let [interval-in-secs (t/in-seconds interval)
        interval-in-mins (if (> interval-in-secs seconds-in-minute)
                           (t/in-minutes interval))
        interval-in-hours (if (> interval-in-mins minutes-in-hour)
                            (t/in-hours interval))
        interval-in-days (if (> interval-in-hours hours-in-day)
                           (t/in-days interval))
        interval-in-years (if (> interval-in-days days-in-year)
                            (t/in-years interval))]
    (or (and interval-in-years
             (pluralize-number interval-in-years "year"))
        (and interval-in-days
             (pluralize-number interval-in-days "day"))
        (and interval-in-hours
             (pluralize-number interval-in-hours "hour"))
        (and interval-in-mins
             (pluralize-number interval-in-mins "minute"))
        (and interval-in-secs
             (pluralize-number interval-in-secs "second")))))

(defn date->days-ago-str
  "Get time interval in secs, mins, hours days or years for a given date time"
  [latest-submission-time]
  (if-let [time latest-submission-time]
    (let [interval (t/interval
                    (l/to-local-date-time time)
                    (l/local-now))]
      (interval->time-str interval))
    nil))

(defn during-today?
  [time-str]
  (t/within? (t/interval (t/today-at 00 00 00)
                         (t/today-at 23 59 59))
             (l/to-local-date-time time-str)))

(defn get-no-submissions-today
  [dataset]
  (count (for [data dataset
               :when (during-today? (:_submission_time data))]
           data)))
