(require '[pallet.api :refer [make-user plan-fn]]
         '[pallet.crate.automated-admin-user :as automated-admin-user]
         '[pallet.crate.java :as java]
         '[pallet.crate.runit :as runit]
         '[pallet.crate.app-deploy :as app-deploy])

(def run-command
  "java -jar /opt/ona-viewer/ona-viewer.jar > /dev/null")

(def ona-viewer-node-spec
  "Server specification for Ona viewer."
  {:image {:os-family :ubuntu
           :os-version-matches "12.04"
           :os-64-bit true
           :image-id "us-east-1/ami-e2861d8b"}
   :location {:location-id "us-east-1a"}
   ;; TODO redirect 80 to 8080 via:
   ;; # iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
   ;; or start server on 80.
   :network {:inbound-ports [22 80 8080]}})

(def ona-viewer-server
  "Group spec with app deploy paths for Ona viewer."
  (group-spec "ona-viewer-server"
              :extends [(java/server-spec {})
                        (runit/server-spec {})
                        (app-deploy/server-spec
                         {:artifacts
                          {:from-lein
                           [{:project-path "target/ona-viewer-%s-standalone.jar"
                             :path "ona-viewer/ona-viewer.jar"}]}
                          :run-command run-command}
                         :instance-id :ona-viewer)]))

(defproject ona-viewer
  :provider {:aws-ec2
             {:node-spec ona-viewer-node-spec
              :phases {:bootstrap
                       (plan-fn (automated-admin-user))}}}
  :groups [ona-viewer-server])
