(ns ona.viewer.templates.modals
  (:use [net.cgrand.enlive-html :only [defsnippet]] :reload))

(defsnippet share-dialog "templates/home.html"
  [:body :div#share_dialog]
  [])