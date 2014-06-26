(ns ona.dataset
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [chan close!]]
            [domina :as dom]
            [domina.events :as ev]
            [goog.net.XhrIo :as xhr]))

(defn GET [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go (>! ch res)
                      (close! ch)))))
    ch))

(defn set-view-event
  "Redirect on click for this view."
  [id]
  (ev/listen! (dom/by-id id)
              :click (fn [event]
                       (let [this (dom/by-id id)
                             url (dom/attr this "data-url")]
                         (go (let [response (<! (GET url))]
                               (js/alert response))))
                       true)))

(defn ^:export init
  "Handle share modal."
  []
  (ev/listen! (dom/by-id "sharing")
              :click (fn [event]
                       identity))
  (doall (map set-view-event ["tab-map" "tab-table" "tab-photo" "tab-chart"])))
