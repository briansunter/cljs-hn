(ns hackernews.scenes.detail-view.events
  (:require [re-frame.core :refer [reg-event-fx reg-fx]]
            [hackernews.interceptors :as i]
            [hackernews.components.react-native.core :as rn]))

(reg-event-fx
 :open-story-external
 i/interceptors
 (fn [cofx [_ story-id]]
   (let [story (get-in cofx [:db :stories story-id])]
     {:db (:db cofx)
      :dispatch [:read-story story-id]
      :open-url-external (:url story)})))

(defn- open-url! [url]
  (.openURL rn/linking url))

(reg-fx
 :open-url-external
 (fn [url]
   (open-url! url)))

(reg-event-fx
 :share-story
 i/interceptors
 (fn [cofx [_ story-id]]
   (let [story (get-in cofx [:db :stories story-id])]
     {:db (:db cofx)
      :share-url {:url (:url story) :message (:title story)}})))

(reg-fx
 :share-url
 (fn [{:keys [url message]}]
   (.share rn/share (clj->js {:url url :message message}))))
