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
        [ona.viewer.templates.modals :only [share-dialog]] :reload)
  (:require [ona.viewer.templates.datasets :as datasets]
            [ona.viewer.urls :as u]))

(defsnippet home-content "templates/home.html"
  [:body :div#content]
  [profile datasets dataset-details query orgs]
  [:.username] (content (:username profile))
  [:div.datasets-table] (content (datasets/datasets-table datasets
                                                          profile))

  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href u/project-new)

  ;; Dataset details
  [:span#public-datasets] (content (str (:no-of-public dataset-details)))
  [:span#private-datasets] (content (str (:no-of-private dataset-details)))

  ;; Search Form
  [:form#search-form] (set-attr :action "/search")
  [:input#search-query] (set-attr :value query)

  ;; Set right hand org nav links
  [:span.organization-links [:a]] (clone-for [org orgs]
                                             [:a] (do-> (set-attr :href (u/org org))
                                                        (content (:name org)))))
