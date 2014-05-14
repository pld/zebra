(ns ona.viewer.wrappers
  (:use [ona.viewer.views.home :only [sign-in]])
  (:require [ring.middleware.logger :as logger]))

(defn wrap-basic-authentication
  [handler]
  (fn [request]
    (if (or (:account (:session request) (= :post (:request-method request))))
      (handler request)
      {:status 200
       :body (sign-in)})))

(defn wrap-logger [handler verbose?]
  (if verbose?
    (logger/wrap-with-logger handler "/dev/stdout")
    (fn [request] (handler request))))
