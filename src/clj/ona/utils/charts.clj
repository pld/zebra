(ns ona.utils.charts
  (:use [c2.core :only [unify]]
        [hiccup.core :only [html]])
  (:require [c2.scale :as scale]))


(defn- style [& info]
  {:style (.trim (apply str (map #(let [[kwd val] %]
                                   (str (name kwd) ":" val "; "))
                                 (apply hash-map info))))})

(defn generate-bar
  "Generates bar chart from data points and returns in htl formart "
  [points]
  (let [width 500,
        bar-height 20
        ;;TODO Genetate build data map from api data
        data {"A" 1, "B" 2, "C" 4, "D" 3}
        s (scale/linear :domain [0 (apply max (vals data))]
                        :range [0 width])]

    (html [:div.bars
            (unify data (fn [[label val]]
                          [:div (style :heigth (str bar-height "px")
                                       :width (str (s val) "px")
                                       :background-color "red")
                           [:span (style :color "yellow") label]]))])))