(ns ona.api.project
  (:use [clojure.string :only [split]]
        [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "projects")
        project-data (parse-http :post url account
                            {:form-params data})
        id (-> (project-data :url) (split #"/") last)]
    (merge project-data {:id id})))
