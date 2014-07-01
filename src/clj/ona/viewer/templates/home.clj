(ns ona.viewer.templates.home
  (:use [net.cgrand.enlive-html :only [after
                                       but
                                       content
                                       clone-for
                                       defsnippet
                                       do->
                                       first-of-type
                                       set-attr
                                       nth-of-type]] :reload)
  (:require [ona.viewer.templates.projects :as projects]
            [ona.viewer.urls :as u]))

(defsnippet render-home-content "templates/home.html"
  [:body :div#content]
  [profile projects project-details query orgs username]
  [:.avatar] (set-attr :src "/img/avatar.jpg")
  [:.username] (content username)
  [:div#tab-content1] (content (projects/project-list profile
                                                      projects))

  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href (u/project-new username))

  ;; Dataset details
  [:span#public-projects] (content (-> project-details :num-public str))
  [:span#private-projects] (content (-> project-details :num-private str))

  ;; Search Form
  [:form#search-form] (set-attr :action (u/search username))
  [:input#search-query] (set-attr :value query)

  ;; Set right hand org nav links
  [:span.organization-links [:a]] (clone-for [org orgs]
                                             [:a] (do-> (set-attr :href (u/org org))
                                                        (content (:name org))
                                                        (if (not= org (last orgs))
                                                          (after ", ")
                                                          identity))))

(defn home-content
  "Wrapper to build args for home content."
  [profile projects project-details query orgs]
  (render-home-content profile
                       projects
                       project-details
                       query
                       orgs
                       (:username profile)))
