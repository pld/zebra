(ns ona.mapview-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest)]))

(deftest leaflet-exec
  (is (= (leaflet "id" "data") 0)))
