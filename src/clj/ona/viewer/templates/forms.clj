(ns ona.viewer.templates.forms
  (:use [net.cgrand.enlive-html :only [append
                                       attr=
                                       but
                                       clone-for
                                       content
                                       defsnippet
                                       do->
                                       first-of-type
                                       remove-attr
                                       set-attr]]
        [clojure.string :only [join]]:reload)
  (:require [ona.viewer.helpers.sharing :as sharing]
            [ona.viewer.urls :as u]))

(defsnippet login-form "templates/login.html"
  [:body :div.content :> :.signin-form]
  [])

(defsnippet new-organization-form "templates/organization/new.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet new-project-form "templates/project/new.html"
  [:div.new-project-form]
  [owner errors]
  [:#errors] (content errors)
  [:form] (set-attr :action (str "/projects/" owner)))

(defsnippet new-tag-form "templates/dataset/tag.html"
  [:body :div.content :> :.new-tag-form]
  [dataset-id project-id]
  [:form](set-attr :action (u/dataset-tags dataset-id project-id))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet metadata-form "templates/dataset/metadata.html"
  [:body :div.content :> :.dataset-metadata-form]
  [dataset-id project-id metadata]
  [:form] (set-attr :action (u/dataset-metadata dataset-id project-id))
  [:form :#dataset-id] (set-attr :value dataset-id)
  [:form :#project-id] (set-attr :value project-id)
  [:a#back] (set-attr :href (u/dataset-sharing dataset-id project-id))
  [:span#title] (content (:title metadata))
  [:input#form-title] (set-attr :value (:title metadata))
  [:input#description] (set-attr :value (:description metadata))
  [:input#tags] (set-attr :value (join ", " (:tags metadata))))


(defsnippet sharing "templates/dataset/new-sharing.html"
  [:body :div#content]
  [metadata dataset-id project-id]
  [:span#title] (content (:title metadata))
  [:form#form] (set-attr :action u/dataset-sharing-post)
  [[:input (attr= :type "radio")]] (set-attr :name sharing/settings)
  [:input#dataset-id] (set-attr :value dataset-id)
  [:input#project-id] (set-attr :value project-id)
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

(defsnippet public-settings "templates/dataset/settings.html"
  [:tr#public-settings]
  [])

(defsnippet private-settings "templates/dataset/settings.html"
  [:tr#private-settings]
  [users]
  [:select#username [:option (but first-of-type)]] nil
  [:select#username [:option first-of-type]]
  (clone-for [user users]
             [:option] (do->
                        (set-attr :value (:username user))
                        (content
                         (:username user)))))

(defsnippet settings "templates/dataset/settings.html"
  [:body :div#content]
  [metadata dataset-id project-id users owner]
  [:span#title] (content (:title metadata))
  [:img#avatar] (set-attr :src (:gravatar owner))
  [:span#owner] (content (:username owner))
  [:input#dataset-id] (set-attr :value dataset-id)
  [:input#project-id] (set-attr :value project-id)
  [:tr#public-settings] nil
  [:tr#private-settings] nil

  [:#dataset-settings :tbody] (append
                                (if (:public metadata)
                                  (public-settings)
                                  (private-settings users)))
  [:a#back](set-attr :href (u/dataset dataset-id project-id)))

(defsnippet sign-up-form "templates/sign-up.html"
  [:body :div#content]
  [])
