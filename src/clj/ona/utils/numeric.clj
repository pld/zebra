(ns ona.utils.numeric
  (:use [inflections.core :only [plural]]))

(defn pluralize-number
  "Create an appropriately pluralized string prefix by number."
  [number kind]
  (str number
       " "
       (if (= 1 number) kind (plural kind))))
