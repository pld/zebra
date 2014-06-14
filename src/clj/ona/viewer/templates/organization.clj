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

(def profile-username
  (comp :username :profile))

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
                                      (set-attr :href (u/org-members (:org org))))
  [:div.org-details :ul.members [:li (but first-of-type)]] nil
  [:div.org-details
   :ul.members
   [:li first-of-type]] (clone-for [member members] [:li] (content member))

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
  [:tbody [:tr first-of-type]]
  (clone-for [member members]
             [:span.name] (content (-> member
                                       :profile
                                       :name))
             [:a.username] (do-> (content (profile-username member))
                                 (set-attr :href
                                           (-> member profile-username u/profile)))
             [:a.dataset-list] (do->
                                (content (str (:num-forms member) " forms"))
                                (set-attr :href
                                          (-> member profile-username u/profile)))
             [:a.remove-link] (set-attr :href
                                        (u/org-remove-member (:org org)
                                                             (profile-username member)))))

(defsnippet teams "templates/organization/teams.html"
  [:body :div#content]
  [org team-details members]
  [:div.myteams] nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam]
  (clone-for [team team-details]
             [:h3 :a.team-name] (do->
                                 (content (-> team :team :name))
                                 (set-attr
                                  :href (u/org-team org
                                                    (:id team))))
             [:span.num-members] (content (-> team :members count str)))
  [:a.members] (do->
                (content (s/postfix-paren-count "Members" team-details))
                (set-attr :href (u/org-members org)))
  [:a.new-team] (set-attr :href (u/org-new-team org))
  [:span.num-teams] (content (-> team-details count str)))

(defsnippet team-info "templates/team/show.html"
  [:body :div#content]
  [org team-id team-info members-info]
  [:.team-name] (content (:name team-info))
  [:div.members] (content (members-table org members-info))
  [:form#add-user] (do->
                    (set-attr :action
                              (u/org-team (:org org)
                                          team-id))
                    (set-attr :method "post"))
  [:form#add-user :input#org](set-attr :value (:org org))
  [:form#add-user :input#teamid](set-attr :value team-id))

(defsnippet new-team "templates/team/new.html"
  [:body :div#content]
  [org]
  [:form#new-team] (do-> (set-attr :action (u/org-new-team (:org org)))
                         (set-attr :method "post"))
  [:form#new-team  :input#org](set-attr :value (:org org)))

(defsnippet members "templates/organization/members.html"
  [:body :div#content]
  [org members teams]
  [:span.num-members] (content (-> members count str))
  [:a#teams] (do->
              (content (s/postfix-paren-count "Teams" teams))
              (set-attr :href (u/org-teams org)))
  [:div.members] (content (members-table org members))
  [:form#adduser] (set-attr :action (u/org-members (:org org))
                           :method "post")
  [:form#adduser :#orgname] (set-attr :value (:org org)))
