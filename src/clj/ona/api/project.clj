(ns ona.api.project
  (:use [clojure.string :only [split]]
        [ona.api.io :only [make-url parse-http]]
        [slingshot.slingshot :only [throw+]]))

(defn all [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "projects")
        project-data (parse-http :post url account
                                 {:form-params data})]
    (if-let [error (:__all__ project-data)]
      (throw+ error)
      (merge project-data
             {:id (-> (project-data :url) (split #"/") last)}))))
