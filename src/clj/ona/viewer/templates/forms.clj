(ns ona.viewer.templates.forms
  (:use [net.cgrand.enlive-html :only [attr=
                                       content
                                       defsnippet
                                       set-attr]] :reload)
  (:require [ona.viewer.sharing :as sharing]
            [ona.viewer.urls :as u]))

(defsnippet login-form "templates/login.html"
  [:body :div.content :> :.signin-form]
  [])

(defsnippet new-organization-form "templates/new-organization.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet new-project-form "templates/new-project.html"
  [:body :div.content :> :.new-project-form]
  [errors]
  [:#errors] (content errors))

(defsnippet new-tag-form "templates/new-tag.html"
  [:body :div.content :> :.new-tag-form]
  [dataset-id]
  [:form](set-attr :action (u/dataset-tags dataset-id))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet metadata-form "templates/dataset-metadata.html"
  [:body :div.content :> :.dataset-metadata-form]
  [dataset-id]
  [:form](set-attr :action (u/dataset-metadata dataset-id))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet sharing "templates/dataset-new-sharing.html"
  [:body :div#content]
  [title dataset-id]
  [:span#title] (content title)
  [:form#form] (set-attr :action u/dataset-sharing-post)
  [[:input (attr= :type "radio")]] (set-attr :name sharing/settings)
  [:input#dataset-id] (set-attr :value dataset-id)
  [:input#private] (set-attr :value sharing/private)
  [:input#open-account] (set-attr :value sharing/open-account)
  [:input#open-all] (set-attr :value sharing/open-all)
  [:input#closed] (set-attr :value sharing/closed))

(defsnippet sign-up-form "templates/sign-up.html"
  [:body :div#content]
  [])
