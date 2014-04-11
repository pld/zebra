(ns ona.viewer.views.datasets
  (:use [hiccup core page]
        [ona.viewer.views.partials :only [base]])
  (:require [ona.api.dataset :as api]))

(defn datasets [account]
  (let [datasets (api/all account)]
    (for [dataset datasets]
      [:p [:a
           {:href (str "/dataset/" (:formid dataset))}
           (:title dataset)]])))

(defn dataset [account dataset-id]
  (let [dataset (api/data account dataset-id)]
    (base
     (for [dataitem dataset]
       [:p (str dataitem)]))))

(defn dataset-new [session])
