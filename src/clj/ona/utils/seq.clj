(ns ona.utils.seq)

(defn diff
  "Return difference between 2 sequences."
  [a b]
  (clojure.set/difference (set a) (set b)))

(defn has-keys?
  "True is map has all these keys."
  [m keys]
  (every? (partial contains? m) keys))

(defn remove-nil
  "Remove nil values from a sequence."
  [l]
  (filter identity l))

(def select-values (comp vals select-keys))

(def select-value (comp first select-values))
