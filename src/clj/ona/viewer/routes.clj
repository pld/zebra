(ns ona.viewer.routes
  (:use compojure.core
        [ona.viewer.views.datasets :only [dataset]]
        [ona.viewer.views.home :only [home-page sign-out submit-sign-in]]
        [ona.viewer.views.profile :only [sign-up submit-sign-up]]
        [hiccup.middleware :only [wrap-base-url]]
        [ona.viewer.views.template :only [base-template]])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ona.viewer.views.projects :as projects]
            [ona.viewer.views.organizations :as organizations]
            [ring.middleware.logger :as logger]))

(defroutes main-routes
  (GET "/" {session :session} (home-page session))
  (POST "/signin" {params :params} (submit-sign-in params))
  (GET "/signout" [] (sign-out))
  (GET "/sign-up" [] (sign-up))
  (POST "/sign-up" {params :params} (submit-sign-up params))
  (GET "/dataset/:id"
       {{account :account} :session
        {id :id} :params}
       (dataset account id))
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
  (GET "/template" request (base-template request))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)
      (#(logger/wrap-with-logger % "/dev/stdout"))))
