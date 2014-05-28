(ns ona.api.io
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(def ^:private meths
  {:get client/get
   :post client/post
   :put client/put})

(def protocol "https")

(def host "stage.ona.io")

(defn make-url
  "Build an API url."
  [& postfix]
  (apply str (concat [protocol "://" host "/api/v1/"] postfix)))

(defn parse-json-response
  [body]
  (json/parse-string body true))

(defn parse-csv-response
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
         {:keys [body]} ((meths method) url options)]
     (if filename
       (parse-csv-response body filename)
       (parse-json-response body)))))
