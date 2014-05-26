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
  [:div.org-details :a.members] (set-attr :href (str  "/organizations/" (:org organization) "/members"))
  [:div.org-details :a.teams] (set-attr :href (str  "/organizations/" (:org organization) "/teams")))


(defsnippet teams "templates/teams.html"
  [:body :div#content]
  [org teams]
  [:div.myteams]nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam] (clone-for [team teams]
                           [:h3](content (:name team)))
  [:a.members] (set-attr :href (str "/organizations/" (:org org) "/members")))

(defsnippet members "templates/members.html"
  [:body :div#content]
  [org members]
  [:table.members [:tr (but first-of-type)]] nil
  [:table.members] (clone-for [member members]
                                       [:h3](content (:name member)))
  [:a.teams] (set-attr :href (str  "/organizations/" (:org org) "/teams"))

  [:form#adduser](set-attr :action (str "/organizations/" (:org org) "/members")
                       :method "post")
  [:form#adduser :#orgname](set-attr :value (:org org)))
