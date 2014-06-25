(ns ona.viewer.templates.accounts
  (:use [net.cgrand.enlive-html :only [defsnippet]] :reload))

(defsnippet login-form "templates/account/login.html"
  [:body :div.content]
  [])

(defsnippet sign-up-form "templates/account/sign-up.html"
  [:body :div#content]
  [])
