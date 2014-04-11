(ns ona.api.io
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def ^:private meths
  {:get http/get
   :post http/post})

(def protocol "https")

(def host "stage.ona.io")

(defn make-url [& postfix]
  (apply str (concat [protocol "://" host "/api/v1/"] postfix)))

(defn parse-http
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