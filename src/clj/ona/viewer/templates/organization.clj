(ns ona.viewer.templates.organization
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       first-of-type
                                       defsnippet
                                       set-attr]] :reload))

(defsnippet profile "templates/org-profile.html"
  [:body :div#content]
  [organization]
  ;; Set organizaion details
  [:div.org-details [:h4 first-of-type]] (content (:name organization))
  [:div.org-details :> :span.city] (content (:city organization))
  [:div.org-details :> :span.country] (content (:country organization))
  [:div.org-details :a.org-url] (content (:url organization))
  [:div.org-details :a.org-url] (set-attr :href (:url organization))
  [:div.org-details :h4.teams :a] (set-attr :href (str (:org organization) "/teams")))


(defsnippet teams "templates/teams.html"
  [:body :div#content]
  [teams]
  [:div.myteams]nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam] (clone-for [team teams]
                           [:h3](content (:name team))))
