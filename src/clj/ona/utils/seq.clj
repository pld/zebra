(ns ona.utils.seq)

(defn remove-nil
  "Remove nil values from a sequence."
  [l]
  (filter identity l))

(def select-values (comp vals select-keys))

(def select-value (comp first select-values))
