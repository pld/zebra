(defproject ona-viewer "0.1.0"
  :description "Ona viewer that connects to the Ona API."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.0"]
                 [ring.middleware.logger "0.4.0"]]
  :plugins [[lein-ring "0.7.1"]]
  :ring {:handler ona.viewer.routes/app})
