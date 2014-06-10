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
                                       nth-of-type]
         :rename {html enlive-html}] :reload
         [ona.viewer.templates.helpers :only [include-js js-tag]]
         [clavatar.core :only [gravatar]])
  (:require [ona.api.organization :as api-orgs]
            [ona.viewer.templates.list-items :as l]
            [ona.viewer.urls :as u]))



(defn- navigation-items
  "Render a nav menu based on user logged in state."
  [logged-in?]
  (if logged-in?
    (array-map "Organizations" "/organizations"
               "Projects" "/projects"
               "Sign-out" "/logout")
    (array-map "Sign-up" "join")))

(defn- build-javascript
  "Render default and custom JavaScript."
  [javascript]
  (if javascript
    (apply enlive-html (concat [(include-js "/js/out/goog/base.js")
                                (include-js "/js/main.js")]
                               javascript))))

(defsnippet link-css (enlive-html [:link {:href "" :rel "stylesheet"}])
  [:link]
  [hrefs]
  (clone-for [href hrefs]
             [:link]
             (set-attr :href href)))

(defsnippet main-menu "templates/base.html"
  [:#main-menu :div.vw-menu]
  [current-path orgs logged-in? username]
  ;; Set user profile link
  [:ul#menu-items
   :li.menu-item
   :div.dropdown
   :a]
  (set-attr :href (str "/profile/" username))

  ;; Remove all but 1 exsiting dropdown menu item
  [:ul#prof-drop [:li but first-of-type]] nil

  ;; Set menu items for user dropdown menu
  [:ul#prof-drop [:li first-of-type]]
  (clone-for [[caption url] (navigation-items logged-in?)]
             [:li] (if (= current-path url)
                     (set-attr :class "active")
                     identity)
             [:li :a] (do-> (content caption)
                            (set-attr :href url)
                            (set-attr :data-test url)))

  ;; Set Home, My Organization links
  [:a#home-link] (set-attr :href "/")
  [:ul#exp-drop [:li (nth-of-type 2)]]
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
  [:head] (append (link-css ["/css/pure-min.css"
                             "/css/font-awesome.min.css"
                             "/css/style.css"]))
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
