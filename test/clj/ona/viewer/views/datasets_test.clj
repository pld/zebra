(ns ona.viewer.views.datasets-test
  (:use midje.sweet
        ona.viewer.views.datasets
        [cheshire.core :only [parse-string]])
  (:require [ona.api.dataset :as api]
            [ona.api.organization :as api-org]
            [ona.api.project :as api-project]
            [ona.api.user :as api-user]
            [ona.viewer.urls :as u]
            [ring.util.response :as response]
            [ona.viewer.helpers.sharing :as sharing]))

(let [owner "owner"]
  (facts "about datasets show"
         "Should returns data for dataset"
         (show :fake-account owner :project-id :dataset-id nil)
         => (contains "Some title")
         (provided
          (api/data :fake-account :dataset-id) => [:row]
          (api/metadata :fake-account :dataset-id)
          => {:title "Some title"
              :last_submission_time "2014-6-3T20:18:23Z"}
          (api/online-data-entry-link :fake-account owner :dataset-id) => ""
          (api-org/all :fake-account) => [])))

(facts "about datasets new"
       "Should return content for creating a dataset"
       (new-dataset :fake-account) =not=> nil

       "Should take project id for project specific dataset"
       (let [project-name "the project name"]
         (new-dataset :fake-account :owner :project-id) => (contains project-name)
         (provided
          (api-project/get-project :fake-account
                                   :owner
                                   :project-id) => {:name project-name}
          (api-org/all :fake-account) => [])))

(fact "about dataset tags"
      "Tags returns all tags for a specific dataset"
      (tags :fake-account :owner :project-id :dataset-id) => (contains (str :fake-tags))
      (provided
       (api/tags :fake-account :dataset-id) => [:fake-tags]
       (api-org/all :fake-account) => [])

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
        (download :fake-account :owner :project-id dataset-id format) => :fake-download
        (provided
          (api/download :fake-account :owner dataset-id) => :file-path
          (get-file :file-path download-name format) => :fake-download
          (api/metadata :fake-account dataset-id) => {:id_string id-string})))

(fact "about dataset metadata"
      "Should show metadata for a specific dataset"
      (let [title "the title"]
        (metadata :fake-account :owner :project-id :dataset-id) => (contains title)
        (provided
         (api/metadata :fake-account :dataset-id) => {:title title}
         (api-org/all :fake-account) => []))

      "Should update metadata for a specific dataset"
      (let [description :description
            title :title
            tags :tags
            dataset-id 1]
        (update :fake-account :owner :project-id dataset-id title description tags)
        => (contains {:status 303})
        (provided
         (api/metadata :fake-account dataset-id) => {}
         (api/update :fake-account
                     dataset-id
                     {:description description})
         => nil
         (api/add-tags :fake-account dataset-id {:tags tags}) => nil)))

(fact "about dataset delete"
      "Should delete a dataset"
      (:status (delete :fake-account :owner :project-id :dataset-id)) => 302
      (provided
       (api/delete :fake-account :owner :dataset-id) => nil))

(fact "about dataset/create"
      "Should return :text value on error"
      (create :fake-account :owner :project-id :file) => :response
      (provided
       (api/create :fake-account :file :owner :project-id) => {:type "alert-error"
                                                      :text :response})

      "Should upload to project and return link to preview URL"
      (parse-string (:body (create :fake-account
                                   :owner
                                   :project-id
                                   :file)) true) =>
      {:preview-url "preview-url"
       :settings-url (u/dataset-sharing :owner :project-id :dataset-id)
       :delete-url (u/dataset-delete :owner :project-id :dataset-id)}
      (provided
       (api/create :fake-account :file :owner :project-id) => {:formid :dataset-id}
       (api/online-data-entry-link :fake-account :owner :dataset-id) => :preview-url))

(fact "about dataset sharing"
      "Should show share settings for a dataset"
      (sharing :fake-account :owner :project-id :dataset-id) => (contains "some form")
      (provided
       (api/metadata :fake-account :dataset-id) => {:title "some form"}
       (api-org/all :fake-account) => [])

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
        (sharing-update :fake-account :owner params-private)
        => (contains {:status 303})
        (provided
         (api/metadata :fake-account dataset-id) => {}
         (api/update :fake-account
                      dataset-id
                      {:downloadable "true"
                       :public_data "false"}) => nil)

        "Should update with open-all setting selected"
        (sharing-update :fake-account :owner params-open)
        => (contains {:status 303})
        (provided
         (api/metadata :fake-account dataset-id) => {}
          (api/update :fake-account
                      dataset-id
                      {:downloadable "true"
                       :public_data "true"}) => nil)))

(let [username  :username
            account {:username username}
            dataset-id :dataset-id
            project-id :project-id
            owner username
            role :role
            params {:dataset-id dataset-id
                    :project-id project-id
                    :username username
                    :owner owner
                    :role role}]
  (fact "about dataset settings"
        "Should show settings for a dataset"
        (settings account :owner :project-id :dataset-id)
        => (every-checker (contains "some form"))
        (provided
         (api/metadata account :dataset-id) => {:title "some form"
                                                      :owner "http://ona/ukanga"}
         (api-user/all account) => []
         (api-user/profile account :owner) => {}
         (api-org/all account) => [])

        "Should not show owner"
        (settings account :owner :project-id :dataset-id)
        =not=> (contains "selected")
        (provided
         (api/metadata account :dataset-id) => {:title "some form"
                                                      :owner "http://ona/ukanga"}
         (api-user/all account) => []
         (api-user/profile account :owner) => {}
         (api-org/all account) => [])

        "Should show owner"
        (settings account :owner :project-id :dataset-id)
        => (every-checker (contains "selected")
                          (contains (str (:username account) " (you)")))
        (provided
         (api/metadata account :dataset-id) => {:title "some form"
                                                      :owner "http://ona/ukanga"}
         (api-user/all account) => []
         (api-user/profile account :owner) => account
         (api-org/all account) => [])

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
          (move-to-project account :owner :project-id :dataset-id)
        => (contains {:status 303})
        (provided
          (api/move-to-project account :dataset-id :project-id username)
          => nil)))
