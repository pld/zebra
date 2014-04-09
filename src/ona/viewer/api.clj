(ns ona.viewer.api
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def protocol "https")

(def host "stage.ona.io")

(defn make-url [postfix]
  (str protocol "://" host "/api/v1/" postfix))

(defn parse-http [url account]
  (let [{:keys [username password]} account
        options {:basic-auth [username password]}
        {:keys [_ _ body error]} @(http/get url options)]
    (if error
      (throw
       (Exception. error))
      (json/parse-string body true))))

(defn projects [account]
  (let [url (make-url "projects")]
    (parse-http url account)))

(defn user-profile [account]
  (let [username (:username account)
        url (make-url (str "profiles/" username))]
    (parse-http url account)))

(defn create-user-profile [params]
  (let [
    {:keys [name username email password password2]} params
    profile {:name name
             :username username
             :email email
             :password password
             :password2 password2 }
    url (make-url "profiles" )
        {:keys [_ _ body error] :as resp} @(http/post url{
                                                          :form-params profile
                                                          :headers {"Authorization" "Token 037d60e45f966d125ebf7a49c5d0616e13db9b60"} })
        ]
        (if error
          (throw
            (Exception. error))
          (json/parse-string body true))))

