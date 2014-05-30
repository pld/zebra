(ns ona.viewer.templates.home
  (:use [net.cgrand.enlive-html :only [append
                                       but
                                       content
                                       clone-for
                                       defsnippet
                                       first-of-type
                                       set-attr
                                       nth-of-type]]
         [ona.viewer.templates.modals :only [share-dialog]] :reload)
  (:require [ona.viewer.templates.datasets :as datasets]))

(defsnippet home-content "templates/home.html"
  [:body :div#content]
  [username datasets]
  [:#username](content username)
  [:div.datasets-table] (content (datasets/datasets-table datasets))
  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href "/dataset")
  [:#sidenav [:a (nth-of-type 2)]] (set-attr :href "/projects")
  [:.modal-wrap](append (share-dialog datasets)))