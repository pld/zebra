(ns ona.viewer.routes_test
  (:use midje.sweet
        ona.viewer.wrappers)
  (:require [ona.viewer.views.home :as home]))

(fact "should parse request and check if session has been set"
        ((->
           :fake-handler
           (wrap-basic-authentication)) :fake-request) => {:status 200
                                                           :body (home/sign-in)})
