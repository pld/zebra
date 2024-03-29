(ns ona.viewer.templates.projects
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       do->
                                       first-of-type
                                       set-attr]]
        [clavatar.core :only [gravatar]]
        [clojure.string :only [join]]
        [ona.utils.numeric :only [pluralize-number]]
        [ona.utils.seq :only [select-value]])
  (:require [ona.viewer.helpers.tags :as t]
            [ona.utils.string :as s]
            [ona.viewer.urls :as u]
            [ona.viewer.templates.datasets :as datasets]))

(defn- last-record-str
  [project]
  (if-let [last-rec (:last-modification project)]
    (join " " ["Last record"
               last-rec
               "ago"])))

(defn- num-datasets-str
  [project]
  (pluralize-number (:num-datasets project)
                    "dataset"))

(defn- project-url
  [project owner]
  (u/project-show owner
                  (-> project :project :url s/last-url-param)))

(defn- user-string
  [logged-in-username shared-username]
  (if (= logged-in-username shared-username)
    (str shared-username " (you)")
    shared-username))

(defsnippet settings "templates/project/settings.html"
  [:body :div.content]
  [owner project owners username shared-users]

  [:#name] (content (:name project))

  ;; Owners select
  [:select#owner [:option (but first-of-type)]] nil
  [:select#owner [:option first-of-type]] (clone-for [owner owners]
                                                      (do-> (content owner)
                                                            (set-attr :value owner)))

  ;; Share list
  [:table#users [:tr (but first-of-type)]] nil
  [:table#users [:tr first-of-type]]
  (clone-for [user shared-users]
             [:.username] (content (user-string username user)))

  ;; Buttons
  [:#back] (set-attr :href "/project")
  [:#done] (set-attr :href (u/project-show owner (:id project))))

(defsnippet show "templates/project/show.html"
  [:body :div.content]
  [owner project forms profile latest-form all-submissions]

  [:#name] (content (:name project))

  ;;Top Nav
  [:div#username] (content (datasets/user-link (:username profile)))
  [:#share-settings] (set-attr :href (u/project-settings owner project))
  [:#add-form] (set-attr :href (u/dataset-new owner (:id project)))

  ;; Side nav
  ;; TODO this will work once the API sends back this content
  [:#description] (content (:description project))
  [:div#project-activity] (content (datasets/activity all-submissions latest-form))

  ;;Project Forms
  [:div.datasets-table] (content (datasets/datasets-table forms
                                                          owner
                                                          (:id project)
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
             [:a.owner-name] (do->
                              (content owner)
                              (set-attr :href (u/profile owner)))
             [:a.project-name] (do->
                                (content (-> project :project :name))
                                (set-attr :href (project-url project owner)))
             [:span.latest] (content (:submissions project))
             [:span.date-created] (content (:date-created project))
             [:span.num-datasets] (content (num-datasets-str project))
             [:span.last-project-modification] (content (last-record-str project))
             [:a.open-link] (set-attr :href (project-url project owner))))

(defn project-list
  "Helper to build arguments for project list template."
  [profile projects]
  (render-project-list profile
                       projects
                       (select-value profile [:org :username])))

(defsnippet new "templates/project/new.html"
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
