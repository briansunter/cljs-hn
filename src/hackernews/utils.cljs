(ns hackernews.utils)

(defn find-first
  [f items]
  (first (filter f items)))

(defn find-by-id
  [id items]
  (find-first #(= id (:id %)) items))

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))
