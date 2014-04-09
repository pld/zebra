(ns ona.viewer.api
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def protocol "https")

(def host "ona.io")

(defn make-url [postfix]
  (str protocol "://" host "/api/v1/" postfix))

(defn user-profile [account]
  (let [{:keys [username password]} account
        url (make-url (str "profiles/" username))
        {:keys [_ _ body error] :as resp} @(http/get url
                                                     {:basic-auth [username
                                                                   password]})]
    (if error
      (throw
       (Exception. error))
      (json/parse-string body true))))
