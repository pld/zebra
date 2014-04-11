(ns ona.api.project
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create [account name]
  (let [owner (make-url "users/" (:username account))
        url (make-url "projects")
        data {:name name
              :owner owner}]
    (parse-http :post url account
                {:form-params data})))
