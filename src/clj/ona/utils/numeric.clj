(ns ona.utils.numeric
  (:use [clojure.string :only [join]]
        [inflections.core :only [plural]]))

(defn pluralize-number
  "Create an appropriately pluralized string prefix by number."
  [number kind]
  (join " " [number (if (= 1 number) kind (plural kind))]))
