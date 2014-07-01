(ns ona.viewer.views.profiles-test
  (:use midje.sweet
        ona.viewer.views.profiles
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]]
        [ona.viewer.helpers.projects :only [project-details]])
  (:require [ona.api.user :as api]
            [ona.api.organization :as api-org]
            [ona.api.project :as api-project]))

(let [name "Some User"
      not-found "Not found."
      username "fake-username"
      password "fake-password"
      account {:username username :password password}
      params {:username username
              :email "fake-email"
              :org "fake-org"
              :country "fake-country"}
      default-profile {:city ""
                       :country ""
                       :email ""
                       :gravatar ""
                       :name ""
                       :org ""
                       :owner ""
                       :require_auth ""
                       :twitter ""
                       :url ""
                       :user ""
                       :username ""
                       :website ""}
      merged-profile (merge default-profile params)]

  (facts "About user-profile"
         "Should show user-profile"
         (profile account username) => (contains name)
         (provided
          (api-org/profile account username) => {:detail true}
          (api/profile account username) => {:name name}
          (project-details account username) => []
          (api-org/all account) => [])

         "Should return error if not found"
         (profile account username) => (contains not-found)
         (provided
          (api/profile account username) =throws=> (slingshot-exception not-found)
          (api-org/profile account username) => {:detail true}))

  (facts "About profile update"
         "Should update user profile"
         (update account params) => (contains {:status 303})
         (provided
          (api/profile account) => default-profile
          (api/update account merged-profile) => :updated-profile)))
