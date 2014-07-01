(ns ona.api.user
  (:use [ona.api.io :only [make-url parse-http]]
        [ona.utils.seq :only [has-keys?]]
        [slingshot.slingshot :only [throw+]]))

(defn profile
  "Return the profile for the account username or the passed username."
  ([account]
     (profile account (:username account)))
  ([account username]
     (let [url (make-url "profiles" username)
           response (parse-http :get url account)]
       (if-let [error (:detail response)]
         (throw+ error)
         response))))

(defn create
  "Create a new user."
  [params]
  (let [profile (select-keys params [:name :username :email :password :password2])
        url (make-url "profiles")
        data {:form-params profile}]
    (parse-http :post url nil data)))

(defn all
  "return all users"
  [account]
  (let [url (make-url "users")]
    (parse-http :get url account)))

(defn update
  "update user profile"
  [account params]
  {:pre [(has-keys? params [:city
                            :country
                            :email
                            :gravatar
                            :name
                            :org
                            :owner
                            :require_auth
                            :twitter
                            :url
                            :user
                            :username
                            :website])]}
  (let [url (make-url "profiles" (:username account))
        data {:form-params params
              :content-type :json}]
    (parse-http :put url account data)))
