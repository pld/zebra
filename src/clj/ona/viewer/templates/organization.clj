(ns ona.viewer.templates.organization
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       first-of-type
                                       defsnippet
                                       set-attr]] :reload))

(defsnippet profile "templates/org-profile.html"
  [:body :div#content]
  [org-details]

  ;; Set Organization details
  [:div.org-details [:h4 first-of-type]] (content (:name (:org org-details)))
  [:div.org-details :> :span.city] (content (:city (:org org-details)))
  [:div.org-details :> :span.country] (content (:country (:org org-details)))
  [:div.org-details :a.org-url] (content (:url (:org org-details)))
  [:div.org-details :a.org-url] (set-attr :href (:url (:org org-details)))
  ;; Set Member details
  [:div.org-details :a.members] (content (str "Members (" (count(:members org-details)) ")"))
  [:div.org-details :a.members] (set-attr :href (str  "/organizations/" (:org (:org org-details)) "/members"))
  [:div.org-details :ul.members [:li (but first-of-type)]] nil
  [:div.org-details :ul.members [:li first-of-type]](clone-for [team (:members org-details)]
                                                                  [:li] (content team))
  ;; Set Team details
  [:div.org-details :a.teams] (content (str "Teams (" (count(:teams org-details)) ")"))
  [:div.org-details :a.teams] (set-attr :href (str  "/organizations/" (:org (:org org-details)) "/teams"))
  [:div.org-details :ul.teams [:li (but first-of-type)]] nil
  [:div.org-details :ul.teams [:li first-of-type]](clone-for [team (:teams org-details)]
                                                               [:li] (content (:name team))))


(defsnippet teams "templates/teams.html"
  [:body :div#content]
  [org teams]
  [:div.myteams]nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam] (clone-for [team teams]
                           [:h3](content (:name team)))
  [:a.members] (set-attr :href (str "/organizations/" (:org org) "/members")))

(defsnippet new-team "templates/new-team.html"
  [:body :div#content]
  [org]
  [:form#new-team](set-attr :action (str "/organizations/" (:org org) "/new-team"))
  [:form#new-team](set-attr :method "post")
  [:form#new-team  :input#org](set-attr :value (:org org)))

(defsnippet members "templates/members.html"
  [:body :div#content]
  [org members]
  [:table.members [:tr (but first-of-type)]] nil
  [:table.members [:tr first-of-type]] (clone-for [member members]
                                       [:span.name](content member)
                                       [:span.username](content member))
  [:a.teams] (set-attr :href (str  "/organizations/" (:org org) "/teams"))

  [:form#adduser](set-attr :action (str "/organizations/" (:org org) "/members")
                       :method "post")
  [:form#adduser :#orgname](set-attr :value (:org org)))
