(ns hackernews.scenes.detail-view.subs
  (:require [re-frame.core :refer [reg-sub]]))

(defn current-route
  [db]
  (let [{:keys [routes index]} (get-in db [:navigation :router-state])]
    (nth routes index)))

(defn detail-story
  [db]
  (let [current-story-id (:story-id (:params (current-route db)))]
    (get-in db [:stories current-story-id])))

(reg-sub
 :detail-story
 (fn [db _]
   (detail-story db)))

(reg-sub
 :current-story-flat-comments
 (fn [db _]
   (let [current-story-id (:id (detail-story db))]
     (filter #(= current-story-id (:story-id %)) (vals (:comments db))))))
