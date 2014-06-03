(ns ona.api.project
  (:use [clojure.string :only [split]]
        [ona.api.io :only [make-url parse-http]]
        [slingshot.slingshot :only [throw+]]))

(defn- add-id
  "Parse and add the projects ID."
  [project-data]
  (merge project-data
         {:id (-> (project-data :url) (split #"/") last)}))

(defn get-forms [account id]
  (let [url (make-url "projects/" (:username account) "/" id "/forms")]
    (parse-http :get url account)))

(defn get-project [account id]
  (let [url (make-url "projects/" (:username account) "/" id)]
    (add-id (parse-http :get url account))))

(defn all [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "projects")
        project-data (parse-http :post url account
                                 {:form-params data})]
    (if-let [error (:__all__ project-data)]
      (throw+ error)
      (add-id project-data))))
