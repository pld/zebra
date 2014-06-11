(ns ona.viewer.templates.forms
  (:use [net.cgrand.enlive-html :only [attr=
                                       content
                                       defsnippet
                                       do->
                                       remove-attr
                                       set-attr]]
        [clojure.string :only [join]]:reload)
  (:require [ona.viewer.sharing :as sharing]
            [ona.viewer.urls :as u]))

(defsnippet login-form "templates/login.html"
  [:body :div.content :> :.signin-form]
  [])

(defsnippet new-organization-form "templates/organization/new.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet new-project-form "templates/project/new.html"
  [:body :div.content :> :.new-project-form]
  [errors]
  [:#errors] (content errors))

(defsnippet new-tag-form "templates/dataset/tag.html"
  [:body :div.content :> :.new-tag-form]
  [dataset-id]
  [:form](set-attr :action (u/dataset-tags dataset-id))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet metadata-form "templates/dataset/metadata.html"
  [:body :div.content :> :.dataset-metadata-form]
  [dataset-id metadata]
  [:form] (set-attr :action (u/dataset-metadata dataset-id))
  [:form :#dataset-id] (set-attr :value dataset-id)
  [:a#back] (set-attr :href (u/dataset-sharing dataset-id))
  [:span#title] (content (:title metadata))
  [:input#form-title] (set-attr :value (:title metadata))
  [:input#description] (set-attr :value (:description metadata))
  [:input#tags] (set-attr :value (join ", " (:tags metadata))))


(defsnippet sharing "templates/dataset/new-sharing.html"
  [:body :div#content]
  [metadata dataset-id]
  [:span#title] (content (:title metadata))
  [:form#form] (set-attr :action u/dataset-sharing-post)
  [[:input (attr= :type "radio")]] (set-attr :name sharing/settings)
  [:input#dataset-id] (set-attr :value dataset-id)
  [:input#private] (do-> (set-attr :value sharing/private)
                         (if-not (:public metadata)
                           (set-attr :checked "checked")
                           (remove-attr :checked)))
  [:input#open-account] (set-attr :value sharing/open-account)
  [:input#open-all] (do->(set-attr :value sharing/open-all)
                         (if (:public metadata)
                           (set-attr :checked "checked")
                           (remove-attr :checked)))
  [:input#closed] (set-attr :value sharing/closed))

(defsnippet sign-up-form "templates/sign-up.html"
  [:body :div#content]
  [])
