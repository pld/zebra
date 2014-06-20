(ns ona.utils.charts
  (:use [c2.core :only [unify]])
  (:require [c2.scale :as scale]))


(defn generate-bar
  "Generates bar chart from data points and returns in htl formart "
  [points]
  (let [width 500,
        bar-height 20
        ;;TODO Genetate build data map from api data
        data {"A" 1, "B" 2, "C" 4, "D" 3}
        s (scale/linear :domain [0 (apply max (vals data))]
                        :range [0 width])]

    [:div.bars
            (unify data (fn [[label val]]
                          [:div {:style {:height bar-height
                                         :width (s val)
                                         :background-color "gray"}}
                           [:span {:style {:color "white"}} label]]))]))