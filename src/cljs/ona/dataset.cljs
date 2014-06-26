(ns ona.dataset
  (:require [domina :as dom]
            [domina.events :as ev]))

(defn ^:export init
  "Handle share modal."
  []
  (ev/listen! (dom/by-id "sharing")
              :click (fn [event]
                       identity))
  (ev/listen! (dom/by-id "tab-table")
              :click (fn [event]
                       (this-as this
                                (set! js/window.location.href
                                      (dom/attr this "data-url")))
                       true)))
