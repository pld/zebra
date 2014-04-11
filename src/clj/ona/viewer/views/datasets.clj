(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.viewer.api :as api]))

(defn datasets [account]
  (let [datasets (api/datasets account)]
    (for [dataset datasets]
      [:p [:a
           {:href (str "/dataset/" (:formid dataset))}
           (:title dataset)]])))

(defn dataset [account dataset-id]
  (let [dataset (api/dataset-getdata account dataset-id)]
    (base
     (for [dataitem dataset]
       [:p (str dataitem)]))))

(defn dataset-new [session])
