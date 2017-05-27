(ns hackernews.api)

(defn- url-encode
  [string]
  (some-> string str (js/encodeURIComponent) (.replace "+" "%20")))

(defn- map->query
  [m]
  (some->> (seq m)
           sort
           (map (fn [[k v]]
                  [(url-encode (name k))
                   "="
                   (url-encode (str v))]))
           (interpose "&")
           flatten
           (apply str)))

(defn- fetch
  [{:keys [url params method body on-success on-failure] :as request}]
  (-> (str url "?" (map->query params))
      (js/fetch (clj->js (select-keys request [:method :body])))
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then #(on-success %))
      (.catch #(on-failure %))))
