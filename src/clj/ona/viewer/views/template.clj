(ns ona.viewer.views.template
  (:use [hiccup core page])
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate main-template "templates/base.html"
  	[]
  	[:body] (html/content "Test Enlive"))

(defn base-template
  [request]
  (main-template))