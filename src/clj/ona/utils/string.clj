(ns ona.utils.string
  (:use [clojure.string :only [join split]]))

(defn substring?
  "True is sub is a subtring of st"
  [sub st]
  ((complement nil?) (re-find (re-pattern sub) st)))

(defn last-url-param
  "Get last parameter form url"
  [url]
  (-> url str (split #"/") last))

(defn postfix-paren-count
  "Wrap the count of a collection in parens and postfix."
  [prefix collection]
  (str prefix
       " ("
       (count collection)
       ")"))

(defn url
  "Append string args with slashes and prefix with a slash."
  [& args]
  (str "/" (join "/" args)))
