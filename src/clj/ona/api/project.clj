(ns ona.api.project
  (:use [clojure.string :only [split]]
        [ona.api.io :only [make-url parse-http]]
        [ona.utils.string :only [last-url-param url]]
        [slingshot.slingshot :only [throw+]]))

(defn- add-id
  "Parse and add the projects ID."
  [project-data]
  (if-let [error (:detail project-data)]
    (throw+ error)
    (merge project-data
           {:id (-> project-data :url last-url-param)})))

(defn get-forms
  "Get the forms for this account and owner of the user."
  [account id]
  (let [url (make-url "projects" id "forms")]
    (parse-http :get url account)))

(defn get-project [account id]
  (let [url (make-url "projects" id)]
    (add-id (parse-http :get url account))))

(defn all
  "Return all project for this account and owner or the user."
  [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create
  "Create a project for this account and owner or the user."
  ([account data]
     (create account data (:username account)))
  ([account data owner]
      (let [owner-url {:owner (make-url "users" owner)}
            url (make-url "projects")
            project-data (parse-http :post url account
                                     {:form-params (merge owner-url data)})]
        (if-let [error (:__all__ project-data)]
          (throw+ error)
          (add-id project-data)))))
