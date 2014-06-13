(ns ona.viewer.templates.projects
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       first-of-type
                                       set-attr]]
        [clavatar.core :only [gravatar]]
        [ona.utils.seq :only [select-value]])
  (:require [ona.utils.string :as s]
            [ona.viewer.urls :as u]
            [ona.viewer.templates.datasets :as datasets]))

(defn- user-string
  [logged-in-username shared-username]
  (if (= logged-in-username shared-username)
    (str shared-username " (you)")
    shared-username))

(defsnippet project-settings "templates/project/settings.html"
  [:body :div.content]
  [owner project username shared-users]

  [:#name] (content (:name project))
  [:#users [:li]] (clone-for [user shared-users]
                             [:.username] (content (user-string username user)))

  ;; Buttons
  [:#back] (set-attr :href "/project")
  [:#done] (set-attr :href (u/project-forms (:id project) owner)))

(defsnippet project-forms "templates/project/forms.html"
  [:body :div.content]
  [owner project forms profile latest-form all-submissions]

  [:#name] (content (:name project))

  ;;Top Nav
  [:div#username] (content (datasets/user-link (:username profile)))
  [:#addform] (set-attr :href (u/project-new-dataset (:id project) owner))

  ;; Side nav
  ;; TODO this will work once the API sends back this content
  [:#description] (content (:description project))
  [:div#project-activity] (content (datasets/activity all-submissions latest-form))

  ;;Project Forms
  [:div.datasets-table] (content (datasets/datasets-table forms
                                                          profile)))

(defsnippet render-project-list "templates/organization/profile.html"
  [:div#tab-inner]
  [profile projects owner]
  ;; Set links
  [:a#new-project] (set-attr :href (u/project-new owner))

  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [project projects]
             [:img.avatar] (set-attr :src (gravatar (:email profile)))
             [:span#owner-name] (content owner)
             [:span#project-name] (content (-> project :project :name))
             [:p#last-project-modification] (content (str
                                                      "Last record "
                                                      (:last-modification project)
                                                      " ago"))
             [:span#no-of-datasets] (content (str (:no-of-datasets project)
                                                  " datasets"))
             [:a#open] (set-attr :href (u/project-forms
                                        (-> project :project :url s/last-url-param)
                                        owner))))

(defn project-list
  "Helper to build arguments for project list template."
  [profile projects]
  (render-project-list profile
                       projects
                       (select-value profile [:org :username])))
