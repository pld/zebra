(ns ona.viewer.templates.profiles
  (:use [net.cgrand.enlive-html :only [content
                                       defsnippet
                                       do->
                                       set-attr]] :reload)
  (:require [ona.viewer.templates.projects :as projects]
            [ona.viewer.urls :as u]))

(defsnippet user-profile "templates/profile/show.html"
  [:body :div#content]
  [profile projects]

  ;; Set user detial on right side bar
  [:h2.username] (content (:name profile))
  [:img.avatar] (set-attr :src (:gravatar profile))
  [:a#settings] (set-attr :href (u/profile-settings (:username profile)))
                 ;(set-attr :href (u/project-new       (:username profile)))
  [:a#new-project] (set-attr :href (u/project-new (:username profile)))
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
  [:label.tab1] (content (str (count projects) " projects"))
  [:div.datasets-table] (content (projects/project-list profile
                                                        projects)))

(defsnippet user-settings "templates/profile/settings.html"
  [:div#profile-settings]
  [profile]
  ;; User settings
  [:input#name] (set-attr :value (:name profile))
  [:input#email] (set-attr :value (:email profile))
  [:input#city] (set-attr :value (:city profile))
  [:input#country] (set-attr :value (:country profile))
  [:input#org] (set-attr :value (:organization profile))
  [:input#website] (set-attr :value (:website profile)))
