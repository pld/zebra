(ns ona.helpers)

(defn slingshot-exception
  [exception-map]
  (slingshot.support/get-throwable
   (slingshot.support/make-context
    exception-map
    (str "throw+: " map)
    (slingshot.support/stack-trace) {})))
