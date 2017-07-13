 (ns hackernews.interceptors
  (:require [re-frame.core :refer [after]]
            [cljs.spec :as s]
            [hackernews.db :as db]))

(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

(def logging
  (after (fn [db [e]] (.log js/console "EVENT" (clj->js e)))))

(def interceptors #_[validate-spec logging])
