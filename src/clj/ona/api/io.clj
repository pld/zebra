(ns ona.api.io
  (:import [com.fasterxml.jackson.core JsonParseException])
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.java.io :as io])
  (:use [slingshot.slingshot :only [try+]]
        [ona.utils.string :only [url]]))

(def ^:private meths
  {:delete client/delete
   :get client/get
   :post client/post
   :put client/put})

(def protocol "https")

(def host "stage.ona.io")

(defn- http-request
  "Send an HTTP request and catch some exceptions."
  [method url options]
  (try+
   (let [{:keys [status body]} ((meths method) url options)]
     {:status status :body body})
   (catch #(<= 400 (:status %)) {:keys [status body]}
     {:status status :body body})))

(defn make-url
  "Build an API url."
  [& postfix]
  (apply str (conj [protocol "://" host "/api/v1"]
                   (apply url postfix))))

(defn parse-json-response
  "Parse a body as JSON catching formatting exceptions."
  [body]
  (try+
   (json/parse-string body true)
   (catch JsonParseException _
       "Improperly formatted API response.")))

(defn parse-csv-response
  "Parse CSV response by writing into a temp file and returning the path."
  [body filename]
  (let [tempfile (java.io.File/createTempFile filename "")
        path (str (.getAbsolutePath tempfile))
        file (clojure.java.io/file path)]
    (.deleteOnExit file)
    (with-open [out-file (io/writer file :append false)]
      (.write out-file body))
    path))

(defn parse-http
  "Send and parse an HTTP response as JSON."
  ([method url account]
   (parse-http method url account {}))
  ([method url account options]
   (parse-http method url account options nil))
  ([method url account options filename]
   (let [options (if-let [{:keys [username password]} account]
                   (assoc options :basic-auth [username password])
                   options)
         {:keys [status body]} (http-request method url options)]
     (if (and filename (< status 400))
       (parse-csv-response body filename)
       (parse-json-response body)))))
