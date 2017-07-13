(ns hackernews.scenes.detail-view.subs
  (:require [re-frame.core :refer [reg-sub]]
            [hackernews.navigation :refer [current-route]]))

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

(reg-sub
 :current-story-parent-comments
 (fn [db _]
   (let [current-story-id (:id (detail-story db))]
     (filter #(= current-story-id (:story-id %) (:parent-id %)) (vals (:comments db))))))

(reg-sub
 :comments-with-parent-id
 (fn [db [_ id]]
   (filter #(= id (:parent-id %)) (vals (:comments db)))))

(defn comments-for-parent-id
  [id comments]
  (filter #(= id (:parent-id %)) comments))

(reg-sub
 :current-story-nested-comments
 (fn [db _]
   (let [current-story (detail-story db)
         current-story-id (:id current-story)
         comments (vals (:comments db))
         root-comments (into #{} (map :id (filter #(= (:parent-id %) current-story-id) comments)))]
     (filter #(root-comments (:id %)) comments))))
