(require '[pallet.actions :refer [exec-script*]]
         '[pallet.api :refer [make-user plan-fn]]
         '[pallet.crate.automated-admin-user :only [automated-admin-user]]
         '[pallet.crate.java :as java]
         '[pallet.crate.runit :as runit]
         '[pallet.crate.app-deploy :as app-deploy])

(def external-port 80)
(def internal-port 8080)

(def iptables-setup
  (str  "iptables -t nat -I PREROUTING -p tcp --dport "
        external-port
        " -j REDIRECT --to-port "
        internal-port))

(def run-command
  "java -jar /opt/ona-viewer/ona-viewer.jar > /dev/null")

(def ona-viewer-node-spec
  "Server specification for Ona viewer."
  {:image {:os-family :ubuntu
           :os-version-matches "12.04"
           :os-64-bit true
           :image-id "us-east-1/ami-e2861d8b"}
   :location {:location-id "us-east-1a"}
   :network {:inbound-ports [22 external-port internal-port]}})

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
                       (plan-fn (automated-admin-user)
                                (exec-script* (iptables-setup)))}}}
  :groups [ona-viewer-server])
