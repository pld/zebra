(ns ona.api.io
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring.util.response :as response]))

(def ^:private meths
  {:get client/get
   :post client/post})

(def protocol "https")

(def host "stage.ona.io")

;TODO remove once https://github.com/onaio/onadata/issues/238 is finished
(def auth_token "037d60e45f966d125ebf7a49c5d0616e13db9b60")

(defn make-url
  "Build an API url."
  [& postfix]
  (apply str (concat [protocol "://" host "/api/v1/"] postfix)))

(defn parse-http
  "Send and parse an HTTP response as JSON."
  ([method url account]
   (parse-http method url account {}))
  ([method url account options]
   (let [options (if-let [{:keys [username password]} account]
                   (assoc options :basic-auth [username password])
                   options)
         {:keys [body]} ((meths method) url options)]
     (json/parse-string body true))))

(defn get-file
  "Send and parse an HTTP response as JSON."
  ([method url account filename]
   (get-file method url account filename {}))
  ([method url account filename options]
   (let [options (if-let [{:keys [username password]} account]
                   (assoc options :basic-auth [username password])
                   options)
         {:keys [body]} ((meths method) url options)
         tempdir (com.google.common.io.Files/createTempDir)
         path (str (.getAbsolutePath tempdir) "/" filename)]
     (.deleteOnExit (clojure.java.io/file path))
     (.deleteOnExit tempdir)
     (with-open [out-file (io/writer path :append false)]
       (.write out-file body))
     (assoc
       (response/file-response path)
       :headers
       {"Content-Type" " text/csv"
        "Content-disposition" (str "attachment;filename=" filename)}))))
