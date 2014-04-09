(ns ona.viewer.routes
  (:use compojure.core
        ona.viewer.views.home
        ona.viewer.views.profile
        [hiccup.middleware :only (wrap-base-url)])
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
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)
      (#(logger/wrap-with-logger % "/dev/stdout"))))
