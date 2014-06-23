(ns ona.utils.charts
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]))


(defn- style [& info]
  {:style (.trim (apply str (map #(let [[kwd val] %]
                                   (str (name kwd) ":" val "; "))
                                 (apply hash-map info))))})

(defn generate-bar
  "Generates bar chart from data points and returns in html formart "
  [chart-data]
  ;; TODO chart generation too slow
  (let [width 500,
        bar-height 20
        field_xpath (:field_xpath chart-data)
        data (:data chart-data)
        extracted-data (apply merge (for [data-item data]
                      {((keyword field_xpath) data-item) (:count data-item)}))
        s (scale/linear :domain [0 (apply max (vals extracted-data))]
                        :range [0 width])]
        [:rect.bars
            (unify extracted-data (fn [[label val]]
                          [:div (style :heigth (str bar-height "px")
                                       :width (str (s val) "px")
                                       :background-color "blue"
                                       :opacity 0.6
                                       :margin "2px")
                           [:span (style :color "yellow") label]]))]))