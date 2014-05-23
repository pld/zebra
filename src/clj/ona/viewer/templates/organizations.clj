(ns ona.viewer.templates.organizations
  (:use [net.cgrand.enlive-html :only [content
                                       first-of-type
                                       defsnippet
                                       set-attr]] :reload))

(defsnippet organization-page "templates/org-profile.html"
  [:body :div#content]
  [organization]
  ;; Set organizaion details
  [:div.org-details [:h4 first-of-type]] (content (:name organization))
  [:div.org-details :> :span.city] (content (:city organization))
  [:div.org-details :> :span.country] (content (:country organization))
  [:div.org-details :a.org-url] (content (:url organization))
  [:div.org-details :a.org-url] (set-attr :href (:url organization)))
