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
            [ona.viewer.helpers.tags :as t]
            [ona.viewer.urls :as u]))

(defsnippet login-form "templates/account/login.html"
  [:body :div.content]
  [])

(defsnippet new-organization-form "templates/organization/new.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet new-project-form "templates/project/new.html"
  [:div.content]
  [owner owners errors]
  [:#errors] (content errors)
  [:form] (set-attr :action (u/project-new owner))

  ;; Owners select
  [:select#owner [:option (but first-of-type)]] nil
  [:select#owner [:option first-of-type]] (clone-for [owner owners]
                                                      (do-> (content owner)
                                                            (set-attr :value owner)))

  [:a#next] (set-attr :href (t/js-submit "project-form")))

(defsnippet new-tag-form "templates/dataset/tag.html"
  [:body :div.content :> :.new-tag-form]
  [owner project-id dataset-id]
  [:form](set-attr :action (u/dataset-tags owner project-id dataset-id))
  [:form :#dataset-id](set-attr :value dataset-id))

(defsnippet metadata-form "templates/dataset/metadata.html"
  [:body :div.content :> :.dataset-metadata-form]
  [owner project-id dataset-id metadata]
  [:form] (set-attr :action (u/dataset-metadata owner project-id dataset-id))
  [:form :#dataset-id] (set-attr :value dataset-id)
  [:form :#project-id] (set-attr :value project-id)
  [:a#back] (set-attr :href (u/dataset-sharing owner project-id dataset-id))
  [:span#title] (content (:title metadata))
  [:input#form-title] (set-attr :value (:title metadata))
  [:input#description] (set-attr :value (:description metadata))
  [:input#tags] (set-attr :value (join ", " (:tags metadata))))


(defsnippet sharing "templates/dataset/new-sharing.html"
  [:body :div#content]
  [metadata owner project-id dataset-id]
  [:span#title] (content (:title metadata))
  [:form#form] (set-attr :action (u/dataset-sharing owner project-id dataset-id))
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
  [:tr#public-settings :td]
  [])

(defsnippet users-shared "templates/dataset/settings.html"
  [:tbody#users :tr]
  [users username]
  [:tr] (clone-for [user users]
                   [:img.avatar] (set-attr :src (:gravatar user))
                   [:span.owner] (content (str (:username user)
                                               (if (= (:username user)
                                                      username)
                                                 " (you)")))
                   [:select.owner [:option.is-owner]] (if (= (:is-owner? user) true)
                                                        (set-attr :selected ""))
                   ))

(defsnippet add-user "templates/dataset/settings.html"
  [:tr#add-user :td]
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
  [metadata dataset-id project-id users shared-users username owner]
  [:span#title] (content (:title metadata))
  [:input#dataset-id] (set-attr :value dataset-id)
  [:input#project-id] (set-attr :value project-id)
  [:tbody#users] (content (users-shared shared-users username))
  [:tr#public-settings] (content (if (:public_data metadata)
                                   (public-settings)
                                   nil))
  [:tr#public-with-link-settings] nil
  ;; TODO use a real conditional for adding users
  [:tr#add-user] (content (if false
                            (add-user users)
                            nil))
  [:a#back](set-attr :href (u/dataset owner project-id dataset-id)))

(defsnippet sign-up-form "templates/account/sign-up.html"
  [:body :div#content]
  [])
