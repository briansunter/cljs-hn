(ns hackernews.scenes.detail-view.subs
  (:require [re-frame.core :refer [reg-sub]]
            [hackernews.utils :refer [find-by-id]]))

(defn current-route
  [db]
  (let [{:keys [routes index]} (get-in db [:navigation :router-state])]
    (nth routes index)))

(defn detail-story
  [db]
  (-> (current-route db)
      :params
      :story-id
      (find-by-id (:stories db))))

(reg-sub
 :detail-story
 (fn [db _]
   (detail-story db)))

(reg-sub
 :current-story-flat-comments
 (fn [db _]
   (let [current-story-id (:id (detail-story db))]
     (filter #(= current-story-id (:story-id %)) (:comments db)))))
