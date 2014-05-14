(ns ona.viewer.routes
  (:use [compojure.core]
        [ona.viewer.views.home :only [home-page sign-out submit-sign-in]]
        [ona.viewer.views.profile :only [sign-up submit-sign-up]]
        [ona.viewer.views.templates :only [base-template]]
        [ona.viewer.wrappers :only [wrap-basic-authentication wrap-logger]])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ona.viewer.views.datasets :as datasets]
            [ona.viewer.views.organizations :as organizations]
            [ona.viewer.views.projects :as projects]
            [ring.adapter.jetty :as ring])
  (:gen-class))

(defroutes main-routes
  (GET "/" {session :session} (home-page session))
  (POST "/signin" {params :params} (submit-sign-in params))
  (GET "/signout" [] (sign-out))
  (GET "/sign-up" [] (sign-up))
  (POST "/sign-up" {params :params} (submit-sign-up params))
  (GET "/dataset" {{account :account} :session} (datasets/new-dataset account))
  (GET "/dataset/:id"
       {{account :account} :session
        {id :id} :params}
       (datasets/show account id))
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
  (GET "/projects"
       {{account :account} :session}
       (projects/all account))
  (POST "/projects"
        {{account :account} :session
         params :params}
        (projects/create account params))
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
  (route/resources "/")
  (route/not-found "Page not found"))

(defn ona-viewer [verbose?]
  (-> (routes main-routes)
      (wrap-basic-authentication)
      (#(wrap-logger % verbose?))
      (handler/site main-routes)))

(def app
  (ona-viewer true))

(def app-production
  (ona-viewer false))

(defn start [port]
  (ring/run-jetty app-production {:port port :join? false}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
