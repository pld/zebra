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
            [ona.viewer.views.profiles :as profiles]
            [ona.viewer.views.projects :as projects]
            [ring.adapter.jetty :as ring])
  (:gen-class))

(defroutes user-routes
  (GET "/join" [] (profiles/sign-up))
  (POST "/join"
        {params :params}
        (profiles/submit-sign-up params))
  (POST "/login"
        {params :params}
        (accounts/submit-login params))
  (GET "/logout" [] (accounts/logout))
  (GET "/profile/:username"
       {{account :account} :session
        {username :username} :params}
       (profiles/user-profile account username)))

(defroutes dataset-routes
  (GET "/dataset"
       {{account :account} :session}
       (datasets/new-dataset account))
  (POST "/dataset"
        {{account :account} :session
         {file :file} :params}
        (datasets/create account file))
  (GET "/search"  {{account :account} :session
                   {query :query} :params}
       (home-page account query))
  (GET "/datasets"
       {{account :account} :session}
       (datasets/show-all account))
  (POST "/dataset/sharing"
        {{account :account} :session
         params :params}
        (datasets/sharing-update account params))
  (GET "/dataset/:id/delete"
       {{account :account} :session
        {id :id} :params}
       (datasets/delete account id))
  (GET "/dataset/:id/download"
       {{account :account} :session
        {id :id} :params}
       (datasets/download account id :csv))
  (GET "/project/:owner/:id/new-dataset"
       {{account :account} :session
        {owner :owner
         project-id :id} :params}
       (datasets/new-dataset account owner project-id))
  (GET "/dataset/:dataset-id/:project-id/show/:context"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id
         context :context} :params}
       (datasets/show account dataset-id project-id (keyword context)))
  (POST "/project/:owner/:id/new-dataset"
        {{account :account} :session
         {file :file
          owner :owner
          project-id :id} :params}
        (datasets/create account file owner project-id))
  (GET "/dataset/:dataset-id/:project-id"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id} :params}
       (datasets/show account dataset-id project-id))
  (GET "/dataset/:dataset-id/:project-id/tags"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id} :params}
       (datasets/tags account dataset-id project-id))
  (POST "/dataset/:dataset-id/:project-id/tags"
        {{account :account} :session
         {dataset-id :dataset-id
          project-id :project-id
          tags :tags} :params}
        (datasets/create-tags account dataset-id project-id tags))
  (GET "/dataset/:dataset-id/:project-id/sharing"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id} :params}
       (datasets/sharing account dataset-id project-id))
  (GET "/dataset/:dataset-id/:project-id/metadata"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id} :params}
       (datasets/metadata account dataset-id project-id))
  (POST "/dataset/:dataset-id/:project-id/metadata"
        {{account :account} :session
         {dataset-id :dataset-id
          project-id :project-id
          description :description
          title :title
          tags :tags} :params}
        (datasets/update account dataset-id project-id title description tags))
  (GET "/dataset/move/:dataset-id/:project-id"
       {{account :account} :session
        {dataset-id :dataset-id
         project-id :project-id} :params}
       (datasets/move-to-project account dataset-id project-id)))

(defroutes project-routes
  (GET "/project/:owner"
       {{account :account} :session
        {owner :owner} :params}
       (projects/new-project account owner))
  (GET "/project/:owner/:id/show"
       {{account :account} :session
        {id :id
         owner :owner} :params}
       (projects/show account owner id))
  (GET "/project/:owner/:id/settings"
       {{account :account} :session
        {id :id
         owner :owner} :params}
       (projects/settings account owner id))
  (POST "/project/:owner"
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
  (GET "/organizations/:name/team/:team-id"
       {{account :account} :session
        {name :name
         team-id :team-id} :params}
       (organizations/team-info account name team-id))
  (POST "/organizations/:name/team/:team-id"
        {{account :account} :session
         {org-name :org
          team-id :teamid
          username :username} :params}
        (organizations/add-team-member account org-name team-id username))
  (GET "/organizations/:name/new-team"
       {{account :account} :session
        {name :name} :params}
       (organizations/new-team account name))
  (POST "/organizations/:name/new-team"
        {{account :account} :session
         params :params}
        (organizations/create-team account params))
  (GET "/organizations/:name/members"
       {{account :account} :session
        {name :name} :params}
       (organizations/members account name))
  (POST "/organizations/:name/members"
        {{account :account} :session
         {org-name :orgname
          member-username :username} :params}
        (organizations/add-member account org-name member-username))
  (POST "/organizations/:name/remove/:member-username"
        {{account :account} :session
         {name :name
          member-username :member-username} :params}
        (organizations/remove-member account name member-username))
  (POST "/organizations/:name/remove/:member-username/:team-id"
        {{account :account} :session
         {name :name
          member-username :member-username
          team-id :team-id} :params}
        (organizations/remove-member account name member-username team-id)))

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
