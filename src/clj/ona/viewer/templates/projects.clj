(ns ona.viewer.templates.projects
  (:use [net.cgrand.enlive-html :only [clone-for
                                       content
                                       defsnippet
                                       set-attr]]))

(defn- user-string
  [logged-in-username shared-username]
  (if (= logged-in-username shared-username)
    (str shared-username " (you)")
    shared-username))

(defsnippet project-settings "templates/project-settings.html"
  [:body :div.content]
  [project username shared-users]

  [:#name] (content (:name project))
  [:#users [:li]] (clone-for [user shared-users]
                             [:.username] (content (user-string username user)))

  ;; Buttons
  [:#back] (set-attr :href "/project")
  [:#done] (set-attr :href (str "/project/" (:id project) "/forms")))

(defsnippet project-forms "templates/project-forms.html"
  [:body :div.content]
  [project]

  [:#name] (content (:name project))
  ;; TODO this will work once the API sends back this content
  [:#description] (content (:description project))
  [:#addform] (set-attr :href "/dataset"))
