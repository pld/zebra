(ns ona.utils.seq)

(defn diff
  "Return difference between 2 sequences."
  [a b]
  (clojure.set/difference (set a) (set b)))

(defn remove-nil
  "Remove nil values from a sequence."
  [l]
  (filter identity l))

(def select-values (comp vals select-keys))

(def select-value (comp first select-values))
