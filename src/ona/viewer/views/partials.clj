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
    [:a {:href "/sign-up"} "Sign up"]
    [:a {:href "/signout"} "Sign out"]
    content]))
