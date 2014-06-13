(ns ona.viewer.templates.profiles
  (:use [net.cgrand.enlive-html :only [content
                                       defsnippet
                                       do->
                                       set-attr]] :reload)
  (:require [ona.viewer.templates.datasets :as dst-templates]))

(defsnippet user-profile "templates/profile/show.html"
  [:body :div#content]
  [profile datasets]

  ;; Set user detial on right side bar
  [:h2.username] (content (:name profile))
  [:img.avatar] (set-attr :src (:gravatar profile))
  [:a#new-project] (set-attr :href "/project")
  [:span.occupation] (content "")
  [:a.org ] (content (:organization profile))
  [:a.website](do-> (content (:website profile))
                    (set-attr :href (:website profile)))
  [:a.twitter] (do-> (content (:twitter profile))
                     (set-attr :href (str "http://www.twitter.com/"
                                          (:twitter profile))))
  [:span.city] (content (:city profile))
  [:span.country] (content (:country profile))
  [:span.works-in] (content "")
  [:span.member-since] (content "")

  ;; Show users datasets
  [:label.tab1] (content (str (count datasets) " datasets"))
  [:div.datasets-table] (content (dst-templates/datasets-table datasets
                                                               (:name profile))))
