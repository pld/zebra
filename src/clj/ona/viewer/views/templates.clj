(ns ona.viewer.views.templates
  (:use [hiccup core page])
  (:require [net.cgrand.enlive-html :as html]))

(def navigation-items
  {"Home" "/"
   "Project" "/projects"
   "Organizations" "/organization"
   "Sign-up" "/sign-up"
   "Sign-out" "/signout"
   })

(html/deftemplate base-template "templates/base.html"
  [{:keys [current-path]} title page-content]
  [:head :title] (html/content title)
  [:body :h1.title] (html/content title)
  [:ul.nav [:li html/first-of-type]] (html/clone-for [[caption url] navigation-items]
                                                     [:li] (if (= current-path url)
                                                             (html/set-attr :class "active")
                                                             identity)
                                                     [:li :a] (html/content caption)
                                                     [:li :a] (html/set-attr :href url))
  [:body :div.content] (html/append page-content))

(html/defsnippet signin-form "templates/sign-in.html"
  [:body :div.content :> :.signin-form]
  [])

(html/defsnippet list-items "templates/list-items.html"
  [:body :div.content :> :.list-items]
  [items url]
  [:p] (html/clone-for [item items]
                       [:p :a] (html/content (:item-name item))
                       [:p :a] (html/set-attr :href (str url (:itemid item)))))

(defn sign-in-form
  []
  (base-template "" "Sign-in" (signin-form)))

(defn dashboard-items
  [username items]
  (base-template "/"
                 (str "Welcome " username " :Dashboard")
                 (list-items items "/dataset/")))
