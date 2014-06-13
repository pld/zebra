(ns ona.utils.seq)

(def select-values (comp vals select-keys))

(def select-value (comp first select-values))
