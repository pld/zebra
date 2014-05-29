(ns ona.viewer.templates.profile
  (:use [net.cgrand.enlive-html :only [content
                                       defsnippet
                                       do->
                                       set-attr]]))

(defsnippet user-profile "templates/user-profile-garrett.html"
  [:body :div#content]
  [profile]
  ;; Set user detial on righ side -bar
  [:h2.username] (content (:name profile))
  [:img.avatar] (set-attr :src (:gravatar profile))
  [:a.new-dataset] (set-attr :href "/dataset")
  [:span.occupation] (content "")
  [:a.org ] (content (:organization profile))
  [:a.website](do-> (content (:website profile))
                    (set-attr :href (:website profile)))
  [:a.twitter] (do-> (content (:twitter profile))
                     (set-attr :href (str "http://www.twitter.com/"(:twitter profile))))
  [:span.city] (content (:city profile))
  [:span.country] (content (:country profile))
  [:span.works-in] (content "")
  [:span.member-since] (content ""))

