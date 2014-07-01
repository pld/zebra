(ns ona.viewer.wrappers
  (:require [ona.viewer.views.accounts :as accounts]
            [ring.middleware.logger :as logger]
            [ring.util.response :as response]))

(def ^:private unprotected-uris
  '("/login"
    "/join"))

(def verbose-path "/dev/stdout")

(def log-path "/var/log/ona-viewer/current")

(defn- authorized?
  "Check that user is authenticated or the process of."
  [request]
  (or (-> request :session :account)
      (not (re-find #"/.+/" (:uri request)))))

(defn wrap-authentication
  "Require authentication if not signing in or up."
  [handler]
  (fn [request]
    (if (authorized? request)
      (handler request)
      (response/redirect "/login"))))

(defn wrap-logger [handler verbose?]
  (logger/wrap-with-logger handler
                           (if verbose? verbose-path log-path)))
