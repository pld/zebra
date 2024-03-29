(ns ona.viewer.templates.organizations
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       do->
                                       first-of-type
                                       defsnippet
                                       set-attr]]
        :reload
        [clavatar.core :only [gravatar]]
        [ona.api.organization :only [owners-team-name single-owner?]])
  (:require [ona.viewer.templates.projects :as project-templates]
            [ona.utils.numeric :as n]
            [ona.utils.string :as s]
            [ona.viewer.urls :as u]))

(def profile-username
  (comp :username :profile))

(defn- can-user-leave-team?
  "Show the leave button if user is a member of team and not only member of the
   Owners team."
  [username team]
  (and (some #{username} (:members team))
       (not (single-owner? (:team team)
                           (:members team)))))

(defsnippet new "templates/organization/new.html"
  [:body :div.content :> :.new-organization-form]
  [])

(defsnippet profile "templates/organization/profile.html"
  [:body :div#content]
  [org members teams project-details is-member?]

  ;; Set Organization details
  [:h4#name] (content (:name org))
  [:span#city] (content (:city org))
  [:span#country] (content (:country org))
  [:p#description] (content (:description org))
  [:a#org-url] (do-> (content (:home_page org))
                     (set-attr :href (:url org)))
  [:div.org-details :img] (set-attr :src (gravatar (:email org)))
  [:a#request-to-join] (if is-member? nil identity)

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
                                    (set-attr :href (u/org-teams (:org org))))
  [:div.org-details :ul.teams [:li (but first-of-type)]] nil
  [:div.org-details
   :ul.teams
   [:li first-of-type]] (clone-for [team teams] [:li] (content (:name team)))

  ;; Organization
  [:td#datasets-count] (content (n/pluralize-number (count project-details)
                                                    "dataset"))
  [:div#tab-content1] (content
                       (project-templates/project-list org
                                                       project-details)))

(defsnippet members-table "templates/organization/members.html"
  [:table.members]
  [org-name members team]
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
                                (content (n/pluralize-number (:num-forms member)
                                                             "form"))
                                (set-attr :href
                                          (-> member profile-username u/profile)))
             [:form.remove-form] (if (single-owner? team members)
                                   nil
                                   (do-> (set-attr :action
                                                    (u/org-remove-member
                                                     org-name
                                                     (profile-username member)
                                                     (:id team)))
                                         (set-attr :method "post")))))

(defsnippet team-header "templates/organization/teams.html"
  [:div#header]
  [org teams members on-page]
  [:a#members] (do->
                (content (s/postfix-paren-count "Members" members))
                (set-attr :href (u/org-members org))
                (if (= :members on-page)
                  (set-attr :class "active")
                  identity))
  [:a#teams] (do->
              (content (s/postfix-paren-count "Teams" teams))
              (set-attr :href (u/org-teams org))
              (if (= :teams on-page)
                (set-attr :class "active")
                identity)))

(defsnippet show-teams "templates/organization/teams.html"
  [:body :div#content]
  [org team-details members username]
  [:div.myteams] nil
  [:div.orgteams [:.orgteam (but first-of-type)]] nil
  [:div.orgteams :.orgteam]
  (clone-for [team team-details]
             [:h3 :a.team-name] (do->
                                 (content (-> team :team :name))
                                 (set-attr
                                  :href (u/org-team org
                                                    (:id team))))
             [:span.num-members] (content (-> team :members count str))
             [:form] (if (can-user-leave-team? username team)
                       (set-attr :action
                                 (u/org-remove-member org username (:id team)))
                       nil))
  [:a.new-team] (set-attr :href (u/org-new-team org))
  [:div#header] (content (team-header org team-details members :teams)))

(defsnippet team-info "templates/team/show.html"
  [:body :div#content]
  [org-name team-id team-info members-info all-teams all-members]
  [:.team-name] (content (:name team-info))
  [:div.members] (content (members-table org-name members-info team-info))
  [:form#add-user] (do->
                    (set-attr :action
                              (u/org-team org-name
                                          team-id))
                    (set-attr :method "post"))
  [:form#add-user :input#org](set-attr :value org-name)
  [:form#add-user :input#teamid](set-attr :value team-id)
  [:div#header] (content (team-header org-name all-teams all-members nil)))

(defsnippet new-team "templates/team/new.html"
  [:body :div#content]
  [org]
  [:form#new-team] (do-> (set-attr :action (u/org-new-team (:org org)))
                         (set-attr :method "post"))
  [:form#new-team  :input#org](set-attr :value (:org org)))

(defsnippet members "templates/organization/members.html"
  [:body :div#content]
  [org-name members teams]
  [:div#header] (content (team-header org-name teams members :members))

  [:div.members] (content (members-table org-name members nil))
  [:form#adduser] (set-attr :action (u/org-members org-name)
                            :method "post")
  [:form#adduser :#orgname] (set-attr :value org-name))
