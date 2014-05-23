(ns ona.viewer.templates.forms
  (:use [net.cgrand.enlive-html :only [defsnippet
                                       set-attr]] :reload))

(defsnippet login-form "templates/login.html"
  [:body :div.content :> :.signin-form]
  [])

(defsnippet new-dataset-form "templates/new-dataset.html"
  [:body :div.content :> :.new-dataset-form]
  [])

(defsnippet new-organization-form "templates/new-organization.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet new-project-form "templates/new-project.html"
  [:body :div.content :> :.new-project-form]
  [])

(defsnippet new-tag-form "templates/new-tag.html"
  [:body :div.content :> :.new-tag-form]
  [dataset-id]
  [:form](set-attr :action (str "/dataset/" dataset-id "/tags"))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet metadata-form "templates/dataset-metadata.html"
  [:body :div.content :> :.dataset-metadata-form]
  [dataset-id]
  [:form](set-attr :action (str "/dataset/" dataset-id "/metadata"))
  [:form :#dataset-id](set-attr :value dataset-id))