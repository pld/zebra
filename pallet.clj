(require '[pallet.api :refer [make-user plan-fn]]
         '[pallet.crate.automated-admin-user :as automated-admin-user]
         '[pallet.crate.java :as java]
         '[pallet.crate.runit :as runit]
         '[pallet.crate.app-deploy :as app-deploy])

(def ona-viewer-node-spec
  {:image {:os-family :ubuntu
           :os-version-matches "12.04"
           :os-64-bit true
           :image-id "us-east-1/ami-e2861d8b"}
   :location {:location-id "us-east-1a"}
   :network {:inbound-ports [22 8080]}})

(def ona-viewer-server
  (group-spec "ona-viewer-server"
              :extends [(java/server-spec {})
                        (runit/server-spec {})
                        (app-deploy/server-spec
                         {:artifacts
                          {:from-lein
                           [{:project-path "target/ona-viewer-%s-standalone.jar"
                             :path "ona-viewer.jar"}]}
                          :run-command "java -jar /opt/ona-viewer/ona-viewer.jar"}
                         :instance-id :ona-viewer)]))

(defproject ona-viewer
  :provider {:aws-ec2
             {:node-spec ona-viewer-node-spec
              :phases {:bootstrap
                       (plan-fn (automated-admin-user)
                                (make-user "runit"))}}}
  :groups [ona-viewer-server])
