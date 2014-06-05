(ns ona.viewer.utils.utils
  (:require [clj-time.core :as t]
           [clj-time.local :as l]))

(defn get-now-interval
  "Get time interval in secs, mins, hours days or years for a given submisssion time"
  [latest-submission-time]

  (let [interval (t/interval
                   (l/to-local-date-time latest-submission-time)
                   (l/local-now))
        interval-in-secs (t/in-seconds interval)
        interval-in-mins (if (> interval-in-secs 60)
                           (t/in-minutes interval))
        interval-in-hours (if (> interval-in-mins 60)
                            (t/in-hours interval))
        interval-in-days (if (> interval-in-hours 24)
                            (t/in-days interval))
        interval-in-years (if (> interval-in-days 366)
                           (t/in-years interval))]
    (if interval-in-years
      (str interval-in-years " years")
      (if interval-in-days
        (str interval-in-days " days")
        (if interval-in-hours
          (str interval-in-hours " hours")
          (if interval-in-mins
            (str interval-in-mins " minutes")
            (if interval-in-secs
              (str interval-in-secs " seconds"))))))))
