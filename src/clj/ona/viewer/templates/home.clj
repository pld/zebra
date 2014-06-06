(ns ona.viewer.templates.home
  (:use [net.cgrand.enlive-html :only [append
                                       but
                                       content
                                       clone-for
                                       defsnippet
                                       do->
                                       first-of-type
                                       set-attr
                                       nth-of-type]]
        [ona.viewer.templates.modals :only [share-dialog]] :reload
        [ona.viewer.templates.helpers :only [org-url]])
  (:require [ona.viewer.templates.datasets :as datasets]))

(defsnippet home-content "templates/home.html"
  [:body :div#content]
  [username datasets dataset-details query orgs]
  [:.username] (content username)
  [:div.datasets-table] (content (datasets/datasets-table datasets username))

  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href "/project")

  ;; Dataset details
  [:span#public-datasets] (content (str (:no-of-public dataset-details)))
  [:span#private-datasets] (content (str (:no-of-private dataset-details)))

  ;; Search Form
  [:form#search-form] (set-attr :action "/search")
  [:input#search-query] (set-attr :value query)

  ;; Set right hand org nav links
  [:span.organization-links [:a]] (clone-for [org orgs]
                                             [:a] (do-> (set-attr :href (org-url org))
                                                        (content (:name org)))))
