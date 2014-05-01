(require '[pallet.crate.java :as java]
         '[pallet.crate.runit :as runit]
         '[pallet.crate.app-deploy :as app-deploy])

(def webserver
  (group-spec "webserver"
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
             {:node-spec
              {:image {:os-family :ubuntu
                       :os-version-matches "12.04"
                       :os-64-bit true
                       :image-id "us-east-1/ami-e2861d8b"}
               :location {:location-id "us-east-1a"}
               :network {:inbound-ports [22 8080]}}}}
  :groups [webserver])
