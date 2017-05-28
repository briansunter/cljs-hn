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
   (:comments db)))

(defn current-route
  [db]
  (let [{:keys [routes index]} (get-in db [:navigation :router-state])]
    (nth routes index)))

(defn detail-story
  [db]
  (-> (current-route db)
      :params
      :story-id
      (story-by-id (:stories db))))

(reg-sub
 :detail-story
 (fn [db _]
   (detail-story db)))

(reg-sub
 :current-story-flat-comments
 (fn [db _]
   (:comments db)))

(reg-sub
 :nav-state
 (fn [db _]
   (get-in db [:navigation :router-state])))
