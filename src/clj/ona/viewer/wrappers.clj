(ns ona.viewer.wrappers
  (:require [ona.viewer.views.accounts :as accounts]
            [ring.middleware.logger :as logger]))

(def ^:private unprotected-uris
  '("/login"
    "/join"))

(defn- authorized?
  "Check that user is authenticated or the process of."
  [request]
  (or (-> request :session :account)
      (some #{(:uri request)} unprotected-uris)))

(defn wrap-basic-authentication
  "Require authentication if not signing in or up."
  [handler]
  (fn [request]
    (if (authorized? request)
      (handler request)
      {:status 200
       :body (accounts/login)})))

(defn wrap-logger [handler verbose?]
  (if verbose?
    (logger/wrap-with-logger handler "/dev/stdout")
    (fn [request] (handler request))))
