(ns ona.api.dataset
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "forms")]
    (parse-http :get url account)))

(defn update [account dataset-id params]
  (let [url (make-url "forms/" dataset-id)]
    (parse-http :put url account {:form-params params})))

(defn data [account dataset-id]
  (let [url (make-url "data/" (:username account) "/" dataset-id)]
    (parse-http :get url account)))

(defn submission [account dataset-id submission-id]
  (let [url (make-url "data/" (:username account) "/" dataset-id "/" submission-id)]
    (parse-http :get url account)))

(defn tags [account dataset-id]
  (let [url (make-url "forms/" (:username account) "/" dataset-id "/" "labels")]
    (parse-http :get url account)))

(defn add-tag [account dataset-id tag]
  (let [url (make-url "forms/" (:username account) "/" dataset-id "/" "labels")]
    (parse-http :post url account {:form-params tag})))
