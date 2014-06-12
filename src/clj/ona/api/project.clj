(ns ona.api.project
  (:use [clojure.string :only [split]]
        [ona.api.io :only [make-url parse-http]]
        [slingshot.slingshot :only [throw+]]))

(defn- add-id
  "Parse and add the projects ID."
  [project-data]
  (if-let [error (:detail project-data)]
    (throw+ error)
    (merge project-data
           {:id (-> (project-data :url) (split #"/") last)})))

(defn get-forms [account owner id]
  (let [url (make-url "projects/" owner "/" id "/forms")]
    (parse-http :get url account)))

(defn get-project [account owner id]
  (let [url (make-url "projects/" owner "/" id)]
    (add-id (parse-http :get url account))))

(defn all [account owner]
  (let [url (make-url "projects/" owner)]
    (parse-http :get url account)))

(defn create [account data owner]
  (let [url (make-url "projects/" owner)
        project-data (parse-http :post url account
                                 {:form-params data})]
    (if-let [error (:__all__ project-data)]
      (throw+ error)
      (add-id project-data))))
