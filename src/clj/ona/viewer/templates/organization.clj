(ns ona.viewer.templates.organization
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       do->
                                       first-of-type
                                       defsnippet
                                       set-attr]]
        :reload
        [clavatar.core :only [gravatar]])
  (:require [ona.viewer.templates.projects :as project-templates]
            [ona.utils.string :as s]
            [ona.viewer.urls :as u]))

(defsnippet profile "templates/organization/profile.html"
  [:body :div#content]
  [org members teams project-details]

  ;; Set Organization details
  [:div.org-details [:h4 first-of-type]] (content (:name org))
  [:div.org-details :> :span.city] (content (:city org))
  [:div.org-details :> :span.country] (content (:country org))
  [:div.org-details :a.org-url] (do-> (content (:home_page org))
                                      (set-attr :href (:url org)))
  [:div.org-details :img] (set-attr :src (gravatar (:email org)))

  ;; Set Member details
  [:div.org-details :a.members] (do-> (content (s/postfix-paren-count "Members"
                                                                      members))
                                      (set-attr :href (u/org-members org)))
  [:div.org-details :ul.members [:li (but first-of-type)]] nil
  [:div.org-details
   :ul.members
   [:li first-of-type]] (clone-for [team members] [:li] (content team))

   ;; Set Team details
  [:div.org-details :a.teams] (do-> (content (s/postfix-paren-count "Teams"
                                                                    teams))
                                    (set-attr :href (u/org-teams org)))
  [:div.org-details :ul.teams [:li (but first-of-type)]] nil
  [:div.org-details
   :ul.teams
   [:li first-of-type]] (clone-for [team teams] [:li] (content (:name team)))

   ;; Organization projects
  [:div#tab-content1] (content
                       (project-templates/project-list org
                                                       project-details)))

(defsnippet members-table "templates/organization/members.html"
  [:table.members]
  [org members]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]] (clone-for [member members]
                                          [:span.name](content (:username member))
                                          [:span.username](content (:username member))
                                          [:td :a] (content (str (:num-forms member) " forms"))))

(defsnippet teams "templates/organization/teams.html"
  [:body :div#content]
  [org teams]
  [:div.myteams] nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam] (clone-for [team teams]
                                       [:h3 :a.team-name](do->
                                                          (content (:name team))
                                                          (set-attr
                                                           :href
                                                           (str
                                                            "/organizations/"
                                                            (:org org)
                                                            "/teams/"
                                                            (s/last-url-param (:url team))))))
  [:a.members] (set-attr :href (str "/organizations/" (:org org) "/members"))
  [:a.new-team] (set-attr :href (str "/organizations/" (:org org) "/new-team"))
  [:span.num-teams] (content (str (count teams))))

(defsnippet team-info "templates/team/show.html"
  [:body :div#content]
  [org team-data]
  [:.team-name] (content (:name (:team-info team-data)))
  [:div.members] (content (members-table org (:members-info team-data)))
  [:form#add-user] (do-> (set-attr :action (str "/organizations/" (:org org) "/teams/" (:team-id team-data)))
                         (set-attr :method "post"))
  [:form#add-user :input#org](set-attr :value (:org org))
  [:form#add-user :input#teamid](set-attr :value (:team-id team-data)))

(defsnippet new-team "templates/team/new.html"
  [:body :div#content]
  [org]
  [:form#new-team] (do-> (set-attr :action (str "/organizations/" (:org org) "/new-team"))
                         (set-attr :method "post"))
  [:form#new-team  :input#org](set-attr :value (:org org)))

(defsnippet members "templates/organization/members.html"
  [:body :div#content]
  [org members]
  [:span.num-members] (content (str (count members)))
  [:a#teams] (set-attr :href (str "/organizations/" (:org org) "/teams"))
  [:div.members] (content (members-table org members))
  [:form#adduser] (set-attr :action (str "/organizations/" (:org org) "/members")
                           :method "post")
  [:form#adduser :#orgname] (set-attr :value (:org org)))
