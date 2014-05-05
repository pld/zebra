(ns ona.viewer.views.templates
  (:use [hiccup core page])
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate base-template "templates/base.html"
  [title page-content]
  [:head :title] (html/content title)
  [:body :h1.title] (html/content title)
  [:body :div.content] (html/append page-content))

(html/defsnippet signin-form "templates/sign-in.html"
  [:body :div.content]
  [])

(defn sign-in-form
  []
  (base-template "Sign-in" (signin-form)))
