(ns ona.api.user
  (:use [ona.api.io :only [make-url parse-http]]))

(defn profile [account username]
  (let [url (make-url "profiles/" username)]
    (parse-http :get url account)))

(defn create [params]
  (let [{:keys [name username email password password2]} params
        profile {:name name
                 :username username
                 :email email
                 :password password
                 :password2 password2}
        url (make-url "profiles")
        data {:form-params profile}]
    (parse-http :post url nil data)))
