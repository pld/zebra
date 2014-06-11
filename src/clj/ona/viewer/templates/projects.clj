(ns ona.viewer.templates.projects
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       first-of-type
                                       set-attr]]
        [clavatar.core :only [gravatar]])
  (:require [ona.utils.string :as s]
            [ona.utils.time :as t]
            [ona.viewer.urls :as u]
            [ona.viewer.templates.datasets :as datasets]))

(defn- user-string
  [logged-in-username shared-username]
  (if (= logged-in-username shared-username)
    (str shared-username " (you)")
    shared-username))

(defn- latest-submitted-form
  [forms]
  (let [intervals (for [form forms]
                    { (:formid form)
                      (t/interval->time-int (:last_submission_time form))})
        all-intervals (apply merge intervals)
        latest-formid (key (apply max-key val all-intervals))]
    (for [form forms]
      (if (= (:formid form) latest-formid)
        form))))

(defsnippet project-settings "templates/project/settings.html"
  [:body :div.content]
  [project username shared-users]

  [:#name] (content (:name project))
  [:#users [:li]] (clone-for [user shared-users]
                             [:.username] (content (user-string username user)))

  ;; Buttons
  [:#back] (set-attr :href "/project")
  [:#done] (set-attr :href (str "/project/" (:id project) "/forms")))

(defsnippet project-forms "templates/project/forms.html"
  [:body :div.content]
  [project forms profile]

  [:#name] (content (:name project))

  ;;Top Nav
  [:div#username] (content (datasets/user-link (:username profile)))
  [:#addform] (set-attr :href (str "/project/" (:id project) "/new-dataset"))

  ;; Side nav
  ;; TODO this will work once the API sends back this content
  [:#description] (content (:description project))
  [:div#project-activity] (content (datasets/activity
                                     []
                                     (latest-submitted-form forms)))
  ;[:div#project-activity] (content (latest-submitted-form forms))

  ;;Project Forms
  [:div.datasets-table] (content (datasets/datasets-table forms
                                                          profile)))

(defsnippet project-list "templates/organization/profile.html"
  [:table#projects]
  [org-email projects]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [project projects]
             [:img.avatar] (set-attr :src (gravatar org-email))
             [:span#project-name] (content (:name (:project project)))
             [:p#last-project-modification] (content (str "Last record " (:last-modification project) " ago"))
             [:span#no-of-datasets] (content (str (:no-of-datasets project) " datasets"))
             [:a#open] (set-attr :href (u/project-forms (s/last-url-param (:url (:project project)))))))
