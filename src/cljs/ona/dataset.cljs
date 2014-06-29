(ns ona.dataset
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [<!]]
            [cljs-http.client :as http]
            [domina :as dom]
            [domina.events :as ev]))

(def views ["table" "photo" "chart" "activity"])

(defn load-view-for-node
  [target]
  (let [ url (dom/attr target "data-url")
        content-id (dom/attr target "data-content-id")]
    (go (let [response (<! (http/get url))]
          (dom/set-html! (dom/by-id content-id)
                         (:body response))))))

(defn load-view
  [context]
  (let [target (dom/by-id (str "tab-" context))]
    (load-view-for-node target)))

(defn set-view-event
  "Redirect on click for this view."
  [context]
  (let [tab-id (str "tab-" context)]
    (ev/listen! (dom/by-id tab-id)
                :click (fn [event] (load-view-for-node (:target event))))))

(defn ^:export init
  "Init events on dataset view."
  []
  (ev/listen! (dom/by-id "sharing")
              :click (fn [event]
                       identity))
  (doall (map load-view views))
  ;; TODO do we want to reload on click or just once?
  ;(doall (map set-view-event views))
  )
