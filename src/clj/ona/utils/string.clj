(ns ona.utils.string)

(defn substring?
  "True is sub is a subtring of st"
  [sub st]
  ((complement nil?) (re-find (re-pattern sub) st)))
