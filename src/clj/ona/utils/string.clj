(ns ona.utils.string
  (:use [clojure.string :only [split]]))

(defn substring?
  "True is sub is a subtring of st"
  [sub st]
  ((complement nil?) (re-find (re-pattern sub) st)))

(defn last-url-param
  "Get last parameter form url"
  [url]
  (last (split (str url) #"/")))
