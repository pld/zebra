(ns ona.api.organization
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "orgs")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "orgs")]
    (parse-http :post url account
                {:form-params data})))
