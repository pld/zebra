(defproject ona-viewer "0.1.0-SNAPSHOT"
  :description "Ona viewer that connects to the Ona API."
  :url "https://github.com/onaio/ona-viewer"
  :license {:name "Apache 2 License"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/tools.logging "0.2.6"]
                 [cheshire "5.2.0"]
                 [compojure "1.1.1"]
                 [domina "1.0.2"]
                 [hiccup "1.0.0"]
                 [http-kit "2.1.12"]
                 [http-kit.fake "0.2.1"]
                 [ring.middleware.logger "0.4.0"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [enlive "1.1.5"]]
  :jvm-opts ^:replace ["-Xmx1g"]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}
             :pallet {:dependencies
                      [[com.palletops/pallet "0.8.0-RC.9"]
                       [com.palletops/java-crate "0.8.0-beta.6"]
                       [com.palletops/runit-crate "0.8.0-alpha.3"]
                       [com.palletops/app-deploy-crate "0.8.0-alpha.3"]
                       [com.palletops/pallet-jclouds "1.7.3"]
                       [ch.qos.logback/logback-classic "1.0.9"]
                       [org.jclouds.driver/jclouds-sshj "1.6.0"]
                       [org.slf4j/jcl-over-slf4j "1.7.7"]]}
             :exclusions [commons-logging]
             :uberjar {:aot :all}}
  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-midje "3.1.3"]
            [lein-pdo "0.1.1"]
            [lein-ring "0.7.1"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]]
  :aliases {"up" ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}]}
  :ring {:handler ona.viewer.routes/app}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"])
