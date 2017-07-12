(ns hackernews.utils)

(defn find-first
  [f items]
  (first (filter f items)))

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

(defn update-keys
  [f m]
  (into {} (map (fn [[k v]] (hash-map (f k) v)) m)))

(defn update-values
  [f m]
  (into {} (map (fn [[k v]] (hash-map k (f v))) m)))

(defn index-by
  [f m]
  (update-values first (group-by f m)))

(defn index-by-id
  [m]
  (update-values first (group-by :id m)))

(defn pop-to-one
  [s]
  (if (< 1 (count s)) (pop s) s))
