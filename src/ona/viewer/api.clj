(ns ona.viewer.api
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
     (let [{:keys [username password]} account
           options (assoc options :basic-auth [username password])
           {:keys [_ _ body error]} @((meths method) url options)]
       (if error
         (throw
          (Exception. error))
         (json/parse-string body true)))))

(defn projects [account]
  (let [url (make-url "projects")]
    (parse-http :get url account)))

(defn project-create [account name]
  (let [owner (make-url "users/" (:username account))
        url (make-url "projects")
        data {:name name
              :owner owner}]
    (parse-http :post url account
                {:form-params data})))

(defn user-profile [account]
  (let [username (:username account)
        url (make-url "profiles/" username)]
    (parse-http :get url account)))

(defn datasets [account]
  (let [url (make-url "forms")]
    (parse-http :get url account)))

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
