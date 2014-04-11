(ns ona.viewer.views.home-test
  (:use midje.sweet
        ona.viewer.views.home))

(facts "about home-page"
       "Home page goes to sign in if no session"
       (home-page {}) => :sign-in
       (provided (sign-in) => :sign-in)

       "Home page goes to dashboard if account in session"
       (let [fake-account :fake-account]
         (home-page {:account fake-account}) => :dashboard
         (provided (dashboard fake-account) => :dashboard)))

(facts "about dashboard"
       "Should contain username"
       (let [username "fake-username"]
         (dashboard {:username username}) => (contains username)))
