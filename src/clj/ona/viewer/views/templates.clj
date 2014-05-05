(ns ona.viewer.views.templates
  (:use [hiccup core page])
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate base-template "templates/base.html"
  [title]
  [:head :title] (html/content title)
  [:body :h1.title] (html/content title))

(defn test-base-template
  [request]
  (base-template "lets see" "are we done yet"))
