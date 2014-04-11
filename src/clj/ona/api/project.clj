(ns ona.api.project
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "projects")]
    (parse-http :post url account
                {:form-params data})))
