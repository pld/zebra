(ns ona.viewer.templates.base
  (:use [net.cgrand.enlive-html :only [append
                                       clone-for
                                       content
                                       defsnippet
                                       deftemplate
                                       first-of-type
                                       html
                                       set-attr
                                       nth-of-type]
         :rename {html enlive-html}] :reload)
  (:require [ona.viewer.templates.list-items :as l]))

(def navigation-items
  {"Project" "/projects"
   "Organizations" "/organizations"
  ; "Sign-up" "/join"
   "Sign-out" "/logout"})

(defn build-javascript
  "Render default and custom JavaScript."
  [javascript]
  (let [default-js [[:script {:src "/js/out/goog/base.js" :type "text/javascript"}]
                    [:script {:src "/js/main.js" :type "text/javascript"}]
                    [:script {:type "text/javascript"} "goog.require(\"ona.core\")"]]]
    (apply enlive-html
           (if javascript
             (conj default-js javascript)
             default-js))))

(defsnippet link-css (enlive-html [:link {:href "" :rel "stylesheet"}])
  [:link]
  [hrefs]
  (clone-for [href hrefs]
             [:link]
             (set-attr :href href)))

(defsnippet main-menu "templates/base.html"
  [:#main-menu :div.vw-menu]
  [current-path orgs]
  ;; Set menu items for user dropdown menu
  [:ul#menu-items [:li.menu-item first-of-type] :div.dropdown :ul.submenu [:li first-of-type]]
  (clone-for [[caption url] navigation-items]
             [:li] (if (= current-path url)
                     (set-attr :class "active")
                     identity)
             [:li :a] (content caption)
             [:li :a] (set-attr :href url))
  ;; Set Home, My Organization links
  [:ul#menu-items [:li (nth-of-type 2)] :a](set-attr :href "/")
  [:ul#menu-items [:li (nth-of-type 3)] :div.dropdown :ul.submenu [:li (nth-of-type 2)]]
  (clone-for [organization orgs]
             [:li :a] (set-attr :href (str "organizations/" (:org organization)))
             [:li :a :span.org-name] (content (:name organization))))

(deftemplate render-base-template "templates/base.html"
  [current-path username title orgs page-content javascript]
  ;; Set page title
  [:head :title] (content title)
  ;; Remove CSS and append from /rescources/public/css/
  [:head :link] nil
  [:head] (append (link-css ["/css/pure-min.css"
                             "/css/font-awesome.min.css"
                             "/css/style.css"]))
  ;; Main Menu Items
  [:#main-menu](content (main-menu current-path orgs))
  ;;Page Content
  [:body :div.content-wrap] (content page-content)
  ;;Javascript
  [:body] (append (build-javascript javascript)))

(defn base-template
  "Defines the base template on which page content is appended using snippets"
  ([current-path username title page-content]
   (base-template current-path username title nil page-content nil))
  ([current-path username title orgs page-content]
   (base-template current-path username title orgs page-content nil))
  ([current-path username title orgs page-content javascript]
   (render-base-template current-path username title orgs page-content javascript)))

(defn dashboard-items
  "Renders base template with page-title, username, a list of items and an optional form"
  ([page-title username url items]
   (dashboard-items page-title username url items nil))
  ([page-title username url items form]
   (let [item-list (l/list-items items url)
         page-content (if form
                        (concat form item-list)
                        item-list)]
     (base-template "/"
                    username
                    (str "Dashboard: " page-title)
                    page-content))))
