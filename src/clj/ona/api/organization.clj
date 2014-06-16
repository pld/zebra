(ns ona.api.organization
  (:use [ona.api.io :only [make-url parse-http]]))

(defn all [account]
  (let [url (make-url "orgs")]
    (parse-http :get url account)))

(defn create [account data]
  (let [url (make-url "orgs")]
    (parse-http :post url account
                {:form-params data})))

(defn profile [account org-name]
  (let [url (make-url "orgs" org-name)]
    (parse-http :get url account)))

(defn teams [account org-name]
  (let [url (make-url "teams" org-name)]
    (parse-http :get url account)))

(defn team-info [account org-name team-id]
  (let [url (make-url "teams" org-name team-id)]
    (parse-http :get url account)))

(defn team-members [account org-name team-id]
  (let [url (make-url "teams" org-name team-id "members")]
    (parse-http :get url account)))

(defn create-team
  "Add a team to an organization"
  [account params]
  (let [url (make-url "teams")]
    (parse-http :post url account {:form-params params})))

(defn add-team-member
  "Add a user to a team"
  [account org-name team-id user]
  (let [url (make-url "teams" org-name team-id "members")]
    (parse-http :post url account {:form-params user})))

(defn members [account org-name]
  (let [url (make-url "orgs" org-name "members")]
    (parse-http :get url account)))

(defn add-member
  "Add a user to an organization"
  [account org-name member]
  (let [url (make-url "orgs" org-name "members")]
    (parse-http :post url account {:form-params {:username member}})))

(defn remove-member
  "Remove a user to an organization"
  [account org-name member team-id]
  (let [url (if team-id
              (make-url "orgs" org-name team-id "members")
              (make-url "orgs" org-name "members"))]
    (parse-http :delete url account {:query-params {:username member}})))
