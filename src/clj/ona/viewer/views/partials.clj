(ns ona.viewer.views.partials
  (:use [hiccup core page]))

(defn base [& content]
  (html5
   [:head
    [:title "Ona"]
    (include-css "/css/style.css")]
   [:body
    [:a {:href "/"} "Home"]
    [:br]
    [:a {:href "/projects"} "Projects"]
    [:br]
    [:a {:href "/organizations"} "Organizations"]
    [:br]
    [:a {:href "/join"} "Sign up"]
    [:br]
    [:a {:href "/logout"} "Logout"]
    content
    [:script {:src "js/out/goog/base.js" :type "text/javascript"}]
    [:script {:src "js/main.js" :type "text/javascript"}]
    [:script {:type "text/javascript"} "goog.require(\"ona.core\")"]]))
