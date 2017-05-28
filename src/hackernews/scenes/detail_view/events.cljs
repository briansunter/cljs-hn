(ns hackernews.scenes.detail-view.events
  (:require [re-frame.core :refer [reg-event-fx reg-fx]]
            [hackernews.utils :refer [find-by-id]]
            [hackernews.interceptors :as i]
            [hackernews.components.react-native.core :as rn]))

(reg-event-fx
 :open-story-external
 i/interceptors
 (fn [cofx [_ story-id]]
   (let [story (find-by-id story-id (get-in cofx [:db :stories]))]
     {:dispatch [:read-story story-id]
      :open-url-external (:url story)})))

(defn- open-url! [url]
  (.openURL rn/linking url))

(reg-fx
 :open-url-external
 (fn [url]
   (open-url! url)))
