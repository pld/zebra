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
                      (close! ch))))
              "GET"
              nil
              (clj->js {"Content-Type" "application/json"}))
    ch))

(defn set-view-event
  "Redirect on click for this view."
  [context]
  (let [tab-id (str "tab-" context)
        content-id (str "tab-content" context)]
    (ev/listen! (dom/by-id tab-id)
                :click (fn [event]
                         (let [this (dom/by-id tab-id)
                               url (dom/attr this "data-url")]
                           (go (let [response (<! (GET url))]
                                 (dom/set-html! (dom/by-id content-id)
                                                response))))
                         true))))

(defn ^:export init
  "Init events on dataset view."
  []
  (ev/listen! (dom/by-id "sharing")
              :click (fn [event]
                       identity))
  (doall (map set-view-event ["table" "photo" "chart"])))
