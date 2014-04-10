(ns ona.viewer.views.datasets
  (:use [hiccup core page])
  (:require [ona.viewer.api :as api]))

(defn datasets [account]
  (let [datasets (api/datasets account)]
    (for [dataset datasets] [:p (str dataset)])))
