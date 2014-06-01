(ns ona.mapview
  (:require [domina :as dom]
            [goog.events :as gev]))

(def L (this-as ct (aget ct "L")))

(def center (clj->js [-1.28 36.8]))

(def zoom 7)

(def tile-url
  "http://{s}.tile.osm.org/{z}/{x}/{y}.png")

(def tile-options
  (clj->js {:maxZoom 18
            :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}))

(defn- point-style
 [feature, lat-lng]
 (-> L (.circleMarker
        lat-lng
        (clj->js {:radius 8
                  :fillColor "#ff7800"
                  :color "#000"
                  :weight 1
                  :opacity 1
                  :fillOpacity 1}))))

(defn leaflet
  [id data-var-name]
  (let [data (this-as ct (aget ct data-var-name))
        map (-> L (.map id)
                (.setView center zoom))]
    (-> L (.tileLayer tile-url tile-options)
        (.addTo map))

    (-> L (.geoJson
           data
           (clj->js {:pointToLayer point-style}))
        (.addTo map))))
