(ns ona.viewer.api
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def protocol "https")

(def host "ona.io")

(defn make-url [postfix]
  (str protocol "://" host "/api/v1/" postfix))

(defn parse-http [url options]
  (let [{:keys [_ _ body error]} @(http/get url options)]
    (if error
      (throw
       (Exception. error))
      (json/parse-string body true))))

(defn projects [account])

(defn user-profile [account]
  (let [{:keys [username password]} account
        url (make-url (str "profiles/" username))
        options {:basic-auth [username password]}]
    (parse-http url options)))
