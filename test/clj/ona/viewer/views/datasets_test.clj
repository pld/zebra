(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets
        [cheshire.core :only [parse-string]])
  (:require [ona.api.dataset :as api]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]
            [ona.viewer.urls :as u]
            [ring.util.response :as response]
            [ona.viewer.helpers.sharing :as sharing]))

(facts "about datasets show"
       "Should returns data for dataset"
      (show :fake-account :dataset-id :project-id) => (contains "Some title")
      (provided
        (api/data :fake-account :dataset-id) => [:row]
        (api/metadata :fake-account :dataset-id) => {:title "Some title"
                                                     :last_submission_time "2014-6-3T20:18:23Z"}))

(facts "about datasets new"
      "Should return content for creating a dataset"
      (new-dataset :fake-account) =not=> nil

      "Should take project id for project specific dataset"
      (let [project-name "the project name"]
        (new-dataset :fake-account :owner :project-id) => (contains project-name)
        (provided
         (api-project/get-project :fake-account :owner :project-id) => {:name project-name})))

(fact "about dataset tags"
      "Tags returns all tags for a specific dataset"
      (tags :fake-account :dataset-id :project-id) => (contains (str :fake-tags))
      (provided
        (api/tags :fake-account :dataset-id) => [:fake-tags])

      "Create tags creates tags for a specific dataset"
      (let [tags {:tags "tag1, tag2"}]
        (create-tags :fake-account :dataset-id :project-id tags) => :something
        (provided
         (api/add-tags :fake-account :dataset-id {:tags tags}) => :new-tags
          (response/redirect-after-post
           (u/dataset-tags :dataset-id :project-id)) => :something)))

(fact "about dataset download"
      "Downloads dataset with specified format"
      (let [dataset-id :dataset-id
            format (str "csv")
            id-string :id-string
            download-name (str id-string "." format)]
        (download :fake-account dataset-id format) => :fake-download
        (provided
          (api/download :fake-account dataset-id) => :file-path
          (get-file :file-path download-name format) => :fake-download
          (api/metadata :fake-account dataset-id) => {:id_string id-string})))

(fact "about dataset metadata"
      "Should show metadata for a specific dataset"
      (let [title "the title"]
        (metadata :fake-account :dataset-id :project-id) => (contains title)
        (provided
         (api/metadata :fake-account :dataset-id) => {:title title}))

      "Should update metadata for a specific dataset"
      (let [description :description
            title :title
            tags :tags
            dataset-id 1]
        (update :fake-account dataset-id :project-id title description tags)
        => (contains {:status 303})
        (provided
         (api/metadata :fake-account dataset-id) => {}
         (api/update :fake-account
                     dataset-id
                     {:title title
                      :description description})
         => nil
         (api/add-tags :fake-account dataset-id {:tags tags}) => nil)))

(fact "about dataset delete"
      "Should delete a dataset"
      (:status (delete :fake-account :dataset-id)) => 302
      (provided
       (api/delete :fake-account :dataset-id) => nil))

(fact "about dataset/create"
      "Should return :text value on error"
      (create :fake-account :params) => :response
      (provided
       (api/create :fake-account :params nil nil) => {:type "alert-error"
                                                      :text :response})

      "Should return link to preview URL on success"
      (parse-string (:body (create :fake-account :params)) true) =>
      {:preview-url "preview-url"
       :settings-url (u/dataset-sharing :dataset-id nil)
       :delete-url (u/dataset-delete :dataset-id)}
      (provided
       (api/create :fake-account :params nil nil) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :dataset-id) => :preview-url)

      "Should upload to project if project-id passed"
      (parse-string (:body (create :fake-account
                                   :params
                                   :owner
                                   :project-id)) true) =>
      {:preview-url "preview-url"
       :settings-url (u/dataset-sharing :dataset-id :project-id)
       :delete-url (u/dataset-delete :dataset-id)}
      (provided
       (api/create :fake-account :params :owner :project-id) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :dataset-id) => :preview-url))

(fact "about dataset sharing"
      "Should show share settings for a dataset"
      (sharing :fake-account :dataset-id :project-id) => (contains "some form")
      (provided
        (api/metadata :fake-account :dataset-id) => {:title "some form"})

      "Should update share settings for a dataset"
      (let [dataset-id :dataset-id
            project-id :project-id
            settings-kw  (keyword sharing/settings)
            params-private {:dataset-id dataset-id
                            :project-id project-id
                            settings-kw sharing/private}
            params-open {:dataset-id dataset-id
                         :project-id project-id
                         settings-kw sharing/open-all}]

        "Should update with private setting selected"
        (sharing-update :fake-account params-private)
        => (contains {:status 303})
        (provided
          (api/update :fake-account
                      dataset-id
                      project-id
                      {:shared "False"}) => nil)

        "Should update with open-all setting selected"
        (sharing-update :fake-account params-open)
        => (contains {:status 303})
        (provided
          (api/update :fake-account
                      dataset-id
                      project-id
                      {:shared "True"}) => nil)))

(fact "about dataset settings"
      "Should show settings for a dataset"
      (settings :fake-account :dataset-id :project-id) => (contains "some form")
      (provided
        (api/metadata :fake-account :dataset-id) => {:title "some form" :owner "http://ona/ukanga"}
        (api-user/profile :fake-account "ukanga") => :profile)

      "Should update share settings for a dataset"
      (let [username  :username
            account {:username username}
            dataset-id :dataset-id
            project-id :project-id
            owner username
            role :role
            params {:dataset-id dataset-id
                    :project-id project-id
                    :username username
                    :role role}]

        "Should update with private setting selected"
        (settings-update account params)
        => (contains {:status 303})
        (provided
          (api/update-sharing account
                      dataset-id
                      username
                      owner
                      role) => nil)))

(fact "about move dataset to project"
      (let [username :username
            account {:username username }]
          (move-to-project account :dataset-id :project-id)
        => (contains {:status 303})
        (provided
          (api/move-to-project account :dataset-id :project-id username)
          => nil)))
