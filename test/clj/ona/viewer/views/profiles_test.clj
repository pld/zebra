(ns ona.viewer.views.profiles-test
  (:use midje.sweet
        ona.viewer.views.profiles
        [ona.api.io :only [make-url]]
        [ona.helpers :only [slingshot-exception]])
  (:require [ona.api.user :as api]
            [ona.api.project :as api-project]))

(let [name "Some User"
      not-found "Not found."
      username "fake-username"
      password "fake-password"
      account {:username username :password password}]

  (facts "About user-profile"
         "Should show user-profile"
         (user-profile account username) => (contains name)
         (provided
          (api/profile account username) => {:name name}
          (api-project/all account username) => [{:title "Test dataset"
                                                  :num_of_submissions 2}])

         "Should return error if not found"
         (user-profile account username) => (contains not-found)
         (provided
          (api/profile account username) =throws=> (slingshot-exception not-found))))
