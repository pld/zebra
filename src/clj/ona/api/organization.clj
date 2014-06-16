(ns ona.api.organization
  (:use [ona.api.io :only [make-url parse-http]]))

(def internal-members-team-name "members")

(def owners-team-name "Owners")

(defn single-owner?
  "Is the user the only member of the Owners team."
  ([team members]
     (and (= owners-team-name (-> team :name))
          (= 1 (count members))))
  ([account org-name team-id]
     (single-owner?
      (team-info account org-name team-id)
      (team-members account org-name team-id))))

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

(defn teams-all
  "Return all the teams for an organization."
  [account org-name]
  (let [url (make-url "teams" org-name)]
    (parse-http :get url account)))

(defn teams
  "Return the teams for an organization, removing 'members' team that is used
   internall by the API to store non-team based org members."
  [account org-name]
  (let [teams (teams-all account org-name)]
    (remove #(= internal-members-team-name (:name %)) teams)))

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
  "Remove a user from an organization or organization team"
  [account org-name member team-id]
  (let [url (if team-id
              (make-url "teams" org-name team-id "members")
              (make-url "orgs" org-name "members"))]
    (parse-http :delete url account {:query-params {:username member}})))
