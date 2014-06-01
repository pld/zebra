(ns ona.mapview
  (:require [domina :as dom]
            [goog.events :as gev]))

(def L (this-as ct (aget ct "L")))

(def center (clj->js [-1.28 36.8]))

(def zoom 7)

(def tile-urls
  {:osm
   {:url "http://{s}.tile.osm.org/{z}/{x}/{y}.png"
    :attribution "&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"}
   :mapbox
   {:url "http://{s}.tiles.mapbox.com/v3/modilabs.map-iuetkf9u/{z}/{x}/{y}.png"}})

(defn- tile-options
  "Options for the tile layer."
  [attribution]
  (clj->js {:maxZoom 18
            :attribution attribution}))

(defn- on-feature
  "Execute when a feature is added."
  [feature layer]
  (.bindPopup layer (.-popup (.-properties feature))))

(defn- point-style
  "The style to user for points."
  [feature, lat-lng]
  (-> L (.circleMarker
         lat-lng
         (clj->js {:radius 8
                   :fillColor "#ff3300"
                   :color "#fff"
                   :border 8
                   :opacity 0.5
                   :fillOpacity 0.9}))))

(defn- init-map
  "Initalize a map and default center it."
  [id]
  (-> L (.map id)
      (.setView center zoom)))

(defn- set-tiles
  "Set the tiles on the map."
  [map tile-provider]
  (let [{:keys [url attribution]} (tile-urls tile-provider)]
    (-> L (.tileLayer url (tile-options attribution))
        (.addTo map))))

(defn- load-geo-json
  "Set data as the map's GeoJSON and center on features."
  [map data]
  (let [feature-layer (-> L (.geoJson
                             data
                             (clj->js {:onEachFeature on-feature
                                       :pointToLayer point-style})) (.addTo map))]
    (.fitBounds map (.getBounds feature-layer))))

(defn leaflet
  "Create a leaflet map then add layers and features."
  [id data-var-name]
  (let [data (this-as ct (aget ct data-var-name))
        map (init-map id)]
    (set-tiles map :mapbox)
    (load-geo-json map data)))
