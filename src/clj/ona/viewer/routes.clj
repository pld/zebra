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
  (GET "/:name"
       {{account :account} :session
        {name :name} :params}
       (profiles/profile account name)))

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
  (context "/:owner/:project-id" [owner project-id]
           (GET "/new"
                {{account :account} :session}
                (datasets/new-dataset account owner project-id))
           (POST "/new"
                 {{account :account} :session
                  {file :file} :params}
                 (datasets/create account owner project-id file))
           (context "/:dataset-id" [dataset-id]
                    (GET "/delete"
                         {{account :account} :session}
                         (datasets/delete account owner project-id dataset-id))
                    (GET "/download"
                         {{account :account} :session}
                         (datasets/download account
                                            owner
                                            project-id
                                            dataset-id
                                            :csv))
                    (GET "/metadata"
                         {{account :account} :session}
                         (datasets/metadata account owner project-id dataset-id))
                    (POST "/metadata"
                          {{account :account} :session
                           {description :description
                            title :title
                            tags :tags} :params}
                          (datasets/update account
                                           owner
                                           project-id
                                           dataset-id
                                           title
                                           description
                                           tags))
                    (GET "/move"
                         {{account :account} :session}
                         (datasets/move-to-project account owner project-id dataset-id))
                    (GET "/settings"
                         {{account :account} :session}
                         (datasets/settings account owner project-id dataset-id))
                    (POST "/settings"
                          {{account :account} :session
                           params :params}
                          (datasets/settings-update account params))
                    (GET "/sharing"
                         {{account :account} :session}
                         (datasets/sharing account owner project-id dataset-id))
                    (POST "/sharing"
                          {{account :account} :session
                           params :params}
                          (datasets/sharing-update account owner params))
                    (GET "/tags"
                         {{account :account} :session}
                         (datasets/tags account owner project-id dataset-id))
                    (POST "/tags"
                          {{account :account} :session
                           {dataset-id :dataset-id
                            project-id :project-id
                            tags :tags} :params}
                          (datasets/create-tags account
                                                owner
                                                project-id
                                                dataset-id
                                                tags))
                    (GET "/:context"
                         {{account :account} :session
                          {context :context} :params}
                         (datasets/show account
                                        owner
                                        project-id
                                        dataset-id
                                        (keyword context)))
                    (GET "/"
                         {{account :account} :session}
                         (datasets/show account owner project-id dataset-id)))))

(defroutes project-routes
  (GET "/project/:owner"
       {{account :account} :session
        {owner :owner} :params}
       (projects/new-project account owner))
  (POST "/project/:owner"
        {{account :account} :session
         params :params}
        (projects/create account params))
  (context "/:owner/:id" [owner id]
           (GET "/"
                {{account :account} :session
                 {id :id
                  owner :owner} :params}
                (projects/show account owner id))
           (GET "/settings"
                {{account :account} :session
                 {id :id
                  owner :owner} :params}
                (projects/settings account owner id))))

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
  (GET "/"
       {{account :account} :session}
       (home-page account))
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
