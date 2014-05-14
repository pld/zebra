(ns ona.viewer.views.home-test
  (:use midje.sweet
        ona.viewer.views.home)
  (:require [ona.viewer.views.accounts :as accounts]
            [ona.viewer.views.datasets :as datasets]))


(facts "about home-page"
       "Home page goes to sign in if no session"
       (home-page {}) => :login
       (provided
        (accounts/login) => :login)

       "Home page goes to dashboard if account in session"
       (let [fake-account :fake-account]
         (home-page {:account fake-account}) => :dashboard
         (provided (dashboard fake-account) => :dashboard)))

(facts "about dashboard"
       "Should contain username"
       (let [username "fake-username"
             account {:username username}]
         (dashboard account) => (contains username)
         (provided
          (datasets/all account) => nil)))
