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
  (:require [ona.viewer.templates.projects :as projects]
            [ona.viewer.urls :as u]))

(defsnippet home-content "templates/home.html"
  [:body :div#content]
  [profile projects project-details query orgs]
  [:.username] (content (:username profile))
  [:div#tab-content1] (content (projects/project-list profile
                                                        projects))

  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href (u/project-new (:username profile)))

  ;; Dataset details
  [:span#public-projects] (content (str (:no-of-public project-details)))
  [:span#private-projects] (content (str (:no-of-private project-details)))

  ;; Search Form
  [:form#search-form] (set-attr :action "/search")
  [:input#search-query] (set-attr :value query)

  ;; Set right hand org nav links
  [:span.organization-links [:a]] (clone-for [org orgs]
                                             [:a] (do-> (set-attr :href (u/org org))
                                                        (content (:name org)))))
