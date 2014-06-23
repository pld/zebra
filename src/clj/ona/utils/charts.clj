(ns ona.utils.charts
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]))


(defn- style [& info]
  {:style (.trim (apply str (reduce #(conj %1
                                           (-> %2 first name) ":" (last %2) ";")
                                    []
                                    (partition 2 info))))})

(defn generate-bar
  "Generates bar chart from data points and returns in html formart "
  [chart-data]
  (let [width 500
        bar-height 20
        label (:field_label chart-data)
        field_xpath (:field_xpath chart-data)
        data (:data chart-data)
        extracted-data (into {} (for [data-item data]
                                  [((keyword field_xpath) data-item)
                                   (:count data-item)]))
        s (if (> (count extracted-data) 0)
            (scale/linear :domain [0 (apply max (vals extracted-data))]
                          :range [0 width]))]
    {:label label
     :chart [:rect.bars
             (unify extracted-data (fn [[label val]]
                                     [:div (style :heigth (str bar-height "px")
                                                  :width (str (s val) "px")
                                                  :background-color "blue"
                                                  :opacity 0.6
                                                  :margin "2px")
                                      [:span (style :color "yellow") label]]))]}))
