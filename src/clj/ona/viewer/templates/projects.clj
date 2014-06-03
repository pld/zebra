(ns ona.viewer.templates.projects
  (:use [net.cgrand.enlive-html :only [clone-for
                                       content
                                       defsnippet]]))

(defsnippet project-settings "templates/project-settings.html"
  [:body :div#content]
  [name users]

  [:#name] (content name)
  [:#users [:li]] (clone-for [user users]
                             [:.username (content user)]))
