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

(defn postfix-paren-count
  "Wrap the count of a collection in parens and postfix."
  [prefix collection]
  (str prefix
       " ("
       (count collection)
       ")"))
