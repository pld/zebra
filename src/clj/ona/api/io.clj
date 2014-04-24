(ns ona.api.io
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def ^:private meths
  {:get http/get
   :post http/post})

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
           {:keys [_ _ body error]} @((meths method) url options)]
       (if error
         (throw
          (Exception. error))
         (json/parse-string body true)))))
