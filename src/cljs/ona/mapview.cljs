(ns ona.mapview
  (:require [domina :as dom]
            [goog.events :as gev]))

(def overlay-defaults
  "Default overlay options."
  {:minZ 3
   :maxZ 10
   :tileSize (google.maps.Size. 256 256)})

(defn mk-overlay
  "Returns a Google Maps overlay given the name, url generation function, and
  opacity."
  [name-str url-func opacity]
  (let [opts (clj->js
              (merge overlay-defaults
                     {:name name-str
                      :opacity opacity
                      :getTileUrl url-func}))]
    (google.maps.ImageMapType. opts)))

(def map-opts
  "Default map options."
  {:zoom 5
   :mapTypeId google.maps.MapTypeId.ROADMAP
   :center (google.maps.LatLng. -1, 37)
   :styles [{:stylers [{:visibility "on"}]}]})

(defn init-map
  [element overlays]
  (let [options (clj->js map-opts)
        map (google.maps.Map. element options)
        types (.-overlayMapTypes map)]
    (doseq [layer overlays]
      (.push types layer))
    map))

(defn load-map
  [map-id]
  (letfn [(tile-url [coord zoom]
            (str (.-URL js/document)
                 zoom "/" (.-x coord) "/") (.-y coord) ".png")]
    (init-map
     (dom/by-id map-id)
     [(mk-overlay "iucn" tile-url 0.6)])))

(defn ^:export init
  "Load the map."
  [map-id]
  (gev/listen js/window "load" (fn [event]
                                 (load-map map-id))))
