(ns hackernews.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-front-page-stories
 (fn [db _]
   (:stories db)))

(reg-sub
 :get-story
 (fn [db [_ story-id]]
   (first (filter #(= story-id (:id %)) (:stories db)))))
