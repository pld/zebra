(ns ona.viewer.views.templates
  (:use [hiccup core page])
  (:require [net.cgrand.enlive-html :as html]))

(def navigation-items
  {"Home" "/"
   "New Dataset" "/dataset"
   "Project" "/projects"
   "Organizations" "/organizations"
   "Sign-up" "/sign-up"
   "Sign-out" "/signout"})

(defn build-javascript
  "Render default and custom JavaScript."
  [javascript]
  (let [default-js [[:script {:src "js/out/goog/base.js" :type "text/javascript"}]
                    [:script {:src "js/main.js" :type "text/javascript"}]
                    [:script {:type "text/javascript"} "goog.require(\"ona.core\")"]]]
    (apply html/html
           (if javascript
             (conj default-js javascript)
             default-js))))

(html/deftemplate render-base-template "templates/base.html"
  [current-path username title page-content javascript]
  [:head :title] (html/content title)
  [:body :h1.title] (html/content title)
  [:body :h2.user-details](html/append username)
  [:ul.nav [:li html/first-of-type]] (html/clone-for [[caption url] navigation-items]
                                                     [:li] (if (= current-path url)
                                                             (html/set-attr :class "active")
                                                             identity)
                                                     [:li :a] (html/content caption)
                                                     [:li :a] (html/set-attr :href url))
  [:body :div.content] (html/append page-content)
  [:body] (html/append (build-javascript javascript)))

(defn base-template
  "Defines the base template on which page content it appended using snippets"
  ([current-path username title page-content]
     (base-template current-path username title page-content nil))
  ([current-path username title page-content javascript]
     (render-base-template current-path username title page-content javascript)))

"Snippets are appended to the base template"

"Sign-in form snippet"
(html/defsnippet sign-in-form "templates/sign-in.html"
  [:body :div.content :> :.signin-form]
  [])

"List items snipptet:renders any list of items"
(html/defsnippet list-items "templates/list-items.html"
  [:body :div.content :> :.list-items]
  [items url]
  [:p] (html/clone-for [item items]
                       [:p :a] (html/content (:item-name item))
                       [:p :a] (html/set-attr :href (str url (:item-id item)))
                       [:p] (if (= nil (:item-id item))
                              (html/content (:item-name item))
                              identity)))

(html/defsnippet new-dataset-form "templates/new-dataset.html"
  [:body :div.content :> :.new-dataset-form]
  [])

(html/defsnippet new-organization-form "templates/new-organization.html"
  [:body :div.content :> :.new-organization-form]
  [])

(html/defsnippet new-project-form "templates/new-project.html"
  [:body :div.content :> :.new-project-form]
  [])

(defn dashboard-items
  "Renders base template with page-title, username, a list of items and an optional form"
  ([page-title username url items]
     (dashboard-items page-title username url items nil))
  ([page-title username url items form]
     (let [item-list (list-items items url)
           page-content (if form
                          (concat (form) item-list)
                          item-list)]
       (base-template "/"
                      username
                      (str "Dashboard: " page-title)
                      page-content))))
