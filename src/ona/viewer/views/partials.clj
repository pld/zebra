(ns ona.viewer.views.partials
  (:use [hiccup core page]))

(defn base [& content]
  (html5
   [:head
    [:title "Ona"]
    (include-css "/css/style.css")]
   [:body
    content]))
