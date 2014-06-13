(ns ona.viewer.helpers.tags)

(defn include-js
  "Incude a path to a JavaScript file."
  [path]
  [:script {:src path :type "text/javascript"}])

(defn js-tag
  "Create a JavaScript tag with content."
  [content]
  [:script {:type "text/javascript"} content])
