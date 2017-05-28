(ns hackernews.subs
  (:require  [re-frame.core :refer [reg-sub]]
             [hackernews.utils :refer [find-by-id find-first]]))

(reg-sub
 :get-front-page-stories
 (fn [db _]
   (:stories db)))

(reg-sub
 :get-story
 (fn [db [_ story-id]]
   (find-by-id story-id (:stories db))))

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

(reg-sub
 :nav-state
 (fn [db _]
   (get-in db [:navigation :router-state])))
