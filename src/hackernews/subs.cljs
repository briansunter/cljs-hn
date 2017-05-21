(ns hackernews.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-front-page-stories
 (fn [db _]
   (:stories db)))

(defn- story-by-id
  [id stories]
  (first (filter #(= id (:id %)) stories)))

(reg-sub
 :get-story
 (fn [db [_ story-id]]
   (story-by-id story-id (:stories db))))

(defn- collect-comments
  [comment]
  (filter :content (tree-seq map? :comments comment)))

(reg-sub
 :story-flat-comments
 (fn [db [_ story-id]]
   (collect-comments (story-by-id story-id (:stories db)))))

(reg-sub
 :current-story
 (fn [db _]
   (-> (get-in db [:detail-page :story-id])
       (story-by-id (:stories db)))))

(reg-sub
 :current-story-flat-comments
 (fn [db _]
   (-> (get-in db [:detail-page :story-id])
       (story-by-id (:stories db))
       collect-comments)))
