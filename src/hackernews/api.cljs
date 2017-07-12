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

(defn fetch
  [{:keys [url params method body on-success on-failure response-formatter]
    :or {response-formatter identity}
    :as request}]
  #_(.log js/console "fetching" (clj->js request))
  (-> (str url "?" (map->query params))
      (js/fetch (clj->js (select-keys request [:method :body])))
      (.then #(.json %))
      (.then #(-> % (js->clj :keywordize-keys true) response-formatter on-success))
      (.catch #(on-failure %))))

#_(def result (atom nil))

#_(fetch {:url "https://hn.algolia.com/api/v1/search" :params {:tags "front_page" :page 0} :method "GET":on-success #(reset! result %)})
