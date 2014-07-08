(ns ona.viewer.views.home-test
  (:use midje.sweet
        ona.viewer.views.home
        [clavatar.core :only [gravatar]]
        [midje.util :only [testable-privates]])
  (:require [ona.viewer.views.accounts :as accounts]
            [ona.api.dataset :as api-dataset]
            [ona.api.organization :as api-org]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]))

(testable-privates ona.viewer.views.home move-datasets-to-user-project)

(let [username "fake-username"
      email "fake@email.com"
      account {:username username
               :email email}
      project-id "7"
      form-id "42"]
  (facts "about home-page"
         "Home page goes to sign in if no account"
         (home-page nil) => :login
         (provided
          (accounts/login) => :login)

         "Home page goes to dashboard if account in session"
         (home-page account :fake-search-term) => :dashboard
         (provided (dashboard account :fake-search-term) => :dashboard))

  (facts "about dashboard"
         "Should contain username"
         (dashboard account) => (contains [username email] :in-any-order :gaps-ok)
         (provided
          (api-project/all account nil) =>
          [{:name (str username "'s Project") :url project-id}]
          (api-project/get-forms account project-id) => []
          (api-dataset/all account) => []
          (api-org/all account) => [{:title "Test Org"}]
          (api-user/profile account) => account
          (gravatar email) => email
          (gravatar nil) => nil))

  (facts "about move-datasets-to-user-project"
         "should create project"
         (move-datasets-to-user-project account []) => '(:result)
         (provided
          ;; functions in get-or-create-default-project
          (#'ona.viewer.views.home/default-project-info account) => :info
          (api-project/create account :info) => {:url project-id}
          ;; functions in orphan-datasets
          (api-dataset/all account) => [{:formid form-id}]
          ;; functions in move-datasets-to-user-project
          (api-dataset/move-to-project account form-id project-id) => :result)))
