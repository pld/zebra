(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.routes)
  (:require [ona.viewer.views.datasets :as datasets]))

(fact "should parse account"
      (let [id "1"
            result {:body :something}]
        (main-routes {:request-method :get
                      :uri (str "/dataset/" id)}) => (contains result)
        (provided
         (datasets/dataset nil id) => result)))
