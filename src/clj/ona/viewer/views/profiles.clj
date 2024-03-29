(ns ona.viewer.views.profiles
  (:use [ona.viewer.helpers.projects :only [project-details]]
        [ona.viewer.templates.base :only [base-template]]
        [ona.viewer.templates.accounts :only [sign-up-form]]
        [slingshot.slingshot :only [try+]])
  (:require [ona.api.organization :as api-org]
            [ona.api.user :as api]
            [ring.util.response :as response]
            [ona.viewer.templates.profiles :as profiles]
            [ona.viewer.views.organizations :as orgs]
            [ona.viewer.urls :as u]))

(defn sign-up []
  (base-template "/join" "" "Register" (sign-up-form)))

(defn submit-sign-up
  "Create a new user from submitted profile data."
  [params]
  (let [{:keys [name username email password password2]} params
        profile (api/create params)]
    (base-template
             "/join"
             ""
             "Register"
             (str "Created a profile:" profile))))

(defn- update-hash
  "Build a hash for updates from existing data."
  [account params]
  (merge (api/profile account)
         params))

(defn- user-profile
  "Show the profile for a user."
  [account name]
  (try+
   (let [profile (api/profile account name)
         projects (project-details account name)]
     (base-template
      (u/profile name)
      account
      (:name profile)
      (profiles/user-profile profile projects)))
   (catch string? error
     ;; TODO return a proper not found page
     error)))

(defn- user-settings
  "Show user profile settings"
  [account name]
  (let [profile (api/profile account name)]
    (base-template
      (u/profile name)
      account
      (str (:name profile) " - Settings")
      (profiles/user-settings profile))))

(defn profile
  "Show profile for a username or orgname."
  ([account name]
     (profile account name false))
  ([account name settings]
     (let [org-profile (api-org/profile account name)]
       (if (:detail org-profile)
         (if settings
           (user-settings account name)
           (user-profile account name))
         (orgs/profile account name org-profile)))))

(defn update
  "Update user profile"
  [account params]
  (let [profile (update-hash account params)]
    (api/update account profile)
    (response/redirect-after-post (u/profile (:username account)))))
