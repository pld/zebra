(ns ona.viewer.templates.base
  (:use [net.cgrand.enlive-html :only [append
                                       but
                                       clone-for
                                       content
                                       defsnippet
                                       deftemplate
                                       do->
                                       first-of-type
                                       html
                                       set-attr
                                       nth-of-type]] :reload
         [ona.viewer.helpers.tags :only [include-js js-tag]]
         [clavatar.core :only [gravatar]])
  (:require [ona.api.organization :as api-orgs]
            [ona.viewer.templates.list-items :as l]
            [ona.viewer.urls :as u]))

(def javascript-files
  [ "/js/out/goog/base.js"
    "/js/main.js"])

(def style-sheets
  ["/css/pure-min.css"
   "/css/font-awesome.min.css"
   "/css/style.css"])

(defn- navigation-items
  "Render a nav menu based on user logged in state."
  ([logged-in?]
   (navigation-items logged-in? nil))
  ([logged-in? username]
   (if logged-in?
     (array-map "My Profile" (u/profile username)
                "My Account" (u/profile-settings  username)
                "Sign-out" "/logout")
     (array-map "Sign-up" "join"))))

(defn- build-javascript
  "Render default and custom JavaScript."
  [javascript]
  (if javascript
    (apply html (concat (map include-js javascript-files)
                               javascript))))

(defsnippet link-css (html [:link {:href "" :rel "stylesheet"}])
  [:link]
  [hrefs]
  (clone-for [href hrefs]
             [:link]
             (set-attr :href href)))

(defsnippet main-menu "templates/base.html"
  [:#main-menu :div.vw-menu]
  [current-path orgs logged-in? username]

  ;; Set logo
  [:img#ona-logo] (set-attr :src "/img/ona-logo-sm.png")

  ;; Remove all but 1 exsiting dropdown menu item
  [:ul#exp-drop [:li but first-of-type]] nil

  ;; Set user and menu items for user dropdown menu
  [:span.logged-user] (-> username str content)
  [:ul#exp-drop [:li first-of-type]]
  (clone-for [[caption url] (navigation-items logged-in? username)]
             [:li] (if (= current-path url)
                     (set-attr :class "active")
                     identity)
             [:li :a] (do-> (content caption)
                            (if (= "/projects" url)
                              (set-attr :href (str url "/" username))
                              (set-attr :href url))
                            (set-attr :data-test url)))

  ;; Set Home, My Organization links
  [:a#home-link] (set-attr :href "/")
  [:ul#org-dropdown [:li]]
  (clone-for [org orgs]
             [:li :a] (set-attr :href (u/org org))
             [:li :img] (set-attr :src (gravatar (:email org)))
             [:li :a :span.org-name] (content (:name org))))

(deftemplate render-base-template "templates/base.html"
  [current-path username title orgs page-content javascript logged-in?]
  ;; Set page title
  [:head :title] (content title)
  ;; Remove CSS and append from /rescources/public/css/
  [:head :link] nil
  [:head] (append (link-css style-sheets))
  ;; Main Menu Items
  [:#main-menu](content (main-menu current-path orgs logged-in? username))
  ;; Page Content
  [:body :div.content-wrap] (content page-content)
  ;; Javascript
  [:body] (append (build-javascript javascript)))

(defn base-template
  "Defines the base template on which page content is appended using snippets"
  ([current-path account title page-content]
     (base-template current-path account title page-content nil))
  ([current-path account title page-content javascript]
     (base-template current-path
                    account
                    title
                    page-content
                    javascript
                    (api-orgs/all account)))
  ([current-path account title page-content javascript orgs]
     (let [logged-in? (not= title "Login")
           username (:username account)]
       (render-base-template
        current-path username title orgs page-content javascript logged-in?))))

(defn dashboard-items
  "Renders base template with page-title, username, a list of items and an optional form"
  ([page-title account url items]
   (dashboard-items page-title account url items nil))
  ([page-title account url items form]
   (let [item-list (l/list-items items url)
         page-content (if form
                        (concat form item-list)
                        item-list)]
     (base-template url
                    account
                    (str "Dashboard: " page-title)
                    page-content))))
