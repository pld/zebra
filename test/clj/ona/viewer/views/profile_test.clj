(ns ona.viewer.views.profile-test
  (:use midje.sweet
        ona.viewer.views.profile
        [ona.api.io :only [make-url]])
  (:require [ona.api.user :as api]
            [ona.api.dataset :as api-dataset]))

(let [username "fake-username"
      password "fake-password"
       account {:username username :password password}]

    (fact "user-profile shows user-profile"
          (user-profile account username) => (contains "Some User")
          (provided
            (api/profile account username) => {:name "Some User"}
            (api-dataset/all account) => [:dataset1 :dataset2])))
