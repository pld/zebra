(ns ona.api.organization
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "orgs")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "orgs")]
    (parse-http :post url account
                {:form-params data})))

(defn profile [account org-name]
  (let [url (make-url "orgs/" org-name)]
    (parse-http :get url account)))

(defn teams [account org-name]
  (let [url (make-url "teams/" org-name)]
    (parse-http :get url account)))

(defn members [account org-name]
  (let [url (make-url "orgs/" org-name "/members")]
    (parse-http :get url account)))
