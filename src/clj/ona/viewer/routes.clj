(ns ona.viewer.routes
  (:use compojure.core
        [ona.viewer.views.home :only [home-page sign-out submit-sign-in]]
        [ona.viewer.views.profile :only [sign-up submit-sign-up]]
        [ona.viewer.views.datasets :only [dataset]]
        [hiccup.middleware :only [wrap-base-url]])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ring.middleware.logger :as logger]))

(defroutes main-routes
  (GET "/" {session :session} (home-page session))
  (POST "/signin" {params :params} (submit-sign-in params))
  (GET "/signout" [] (sign-out))
  (GET "/sign-up" [] (sign-up))
  (POST "/sign-up" {params :params} (submit-sign-up params))
  (GET "/dataset/:id" {{account :account} :session
                       {id :id} :params} (dataset account id))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)
      (#(logger/wrap-with-logger % "/dev/stdout"))))
