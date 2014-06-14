(ns ona.api.user
  (:use [ona.api.io :only [make-url parse-http]]
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
  (let [{:keys [name username email password password2]} params
        profile {:name name
                 :username username
                 :email email
                 :password password
                 :password2 password2}
        url (make-url "profiles")
        data {:form-params profile}]
    (parse-http :post url nil data)))
