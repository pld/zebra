(ns ona.viewer.routes
  (:use [compojure.core]
        [ona.viewer.views.home :only [home-page]]
        [ona.viewer.wrappers :only [wrap-basic-authentication wrap-logger]]
        [ring.middleware.resource])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.organizations :as organizations]
            [ona.viewer.views.profile :as profile]
            [ona.viewer.views.projects :as projects]
            [ring.adapter.jetty :as ring])
  (:gen-class))

(defroutes user-routes
  (GET "/join" [] (profile/sign-up))
  (POST "/join" {params :params} (profile/submit-sign-up params))
  (POST "/login" {params :params} (accounts/submit-login params))
  (GET "/logout" [] (accounts/logout))
  (GET "/profile/:username"
       {{account :account} :session
        {username :username} :params} (profile/user-profile account username)))

(defroutes dataset-routes
  (GET "/dataset" {{account :account} :session} (datasets/new-dataset account))
  (GET "/dataset/:id"
       {{account :account} :session
        {id :id} :params}
       (datasets/show account id))
  (GET "/dataset/:id/delete"
       {{account :account} :session
        {id :id} :params}
       (datasets/delete account id))
  (GET "/dataset/:id/tags"
      {{account :account} :session
       {id :id} :params}
      (datasets/tags account id))
  (POST "/dataset/:id/tags"
       {{account :account} :session
        params :params}
       (datasets/create-tags account params))
  (POST "/datasets"
       {{account :account} :session
        {file :file} :params}
       (datasets/create account file))
  (GET "/dataset/:id/download"
      {{account :account} :session
       {id :id} :params}
      (datasets/download account id :csv))
  (GET "/dataset/:id/sharing"
       {{account :account} :session
        {id :id} :params}
       (datasets/sharing account id))
  (POST "/dataset/sharing"
        {{account :account} :session
         params :params}
        (datasets/sharing-update account params))
  (GET "/dataset/:id/metadata"
      {{account :account} :session
       {id :id} :params}
      (datasets/metadata account id))
  (POST "/dataset/:id/metadata"
       {{account :account} :session
        params :params}
       (datasets/update account params)))

(defroutes project-routes
  (GET "/project"
       {{account :account} :session}
       (projects/new-project account))
  (GET "/project/:id/forms"
       {{account :account} :session
        {id :id} :params}
       (projects/forms account id))
  (GET "/project/:id/settings"
       {{account :account} :session
        {id :id} :params}
       (projects/settings account id))
  (GET "/projects"
       {{account :account} :session}
       (projects/all account))
  (POST "/projects"
        {{account :account} :session
         params :params}
        (projects/create account params)))

(defroutes org-routes
  (GET "/organizations"
       {{account :account} :session}
       (organizations/all account))
  (POST "/organizations"
        {{account :account} :session
         params :params}
        (organizations/create account params))
  (GET "/organizations/:name"
       {{account :account} :session
        {name :name} :params}
       (organizations/profile account name))
  (GET "/organizations/:name/teams"
       {{account :account} :session
        {name :name} :params}
       (organizations/teams account name))
  (GET "/organizations/:name/teams/:team-id"
       {{account :account} :session
        {name :name
         team-id :team-id} :params}
       (organizations/team-info account name team-id))
  (POST "/organizations/:name/teams/:team-id"
        {{account :account} :session
         params :params}
        (organizations/add-team-member account params))
  (GET "/organizations/:name/new-team"
       {{account :account} :session
        {name :name} :params}
       (organizations/new-team account name))
  (POST "/organizations/:orgname/new-team"
        {{account :account} :session
         params :params}
        (organizations/create-team account params))
  (GET "/organizations/:name/members"
       {{account :account} :session
        {name :name} :params}
       (organizations/members account name))
  (POST "/organizations/:name/members"
        {{account :account} :session
         params :params}
        (organizations/add-member account params)))

(defroutes main-routes
  (GET "/" {session :session} (home-page session))
  (route/resources "/")
  (route/not-found "Page not found"))

(defroutes app-routes
    org-routes
    user-routes
    dataset-routes
    project-routes
    org-routes
    main-routes)

(defn ona-viewer [verbose?]
  (-> (routes app-routes)
      (wrap-basic-authentication)
      (wrap-resource "public")
      (#(wrap-logger % verbose?))
      (handler/site app-routes)))

(def app
  (ona-viewer true))

(def app-production
  (ona-viewer false))

(defn start [port]
  (ring/run-jetty app-production {:port port :join? false}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
