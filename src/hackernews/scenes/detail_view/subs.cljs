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

(def test-comments [{:id 1 :parent-id 0 :content "foo"}
                    {:id 2 :parent-id 1 :content "bar"}
                    {:id 3 :parent-id 2 :content "bas"}])

(defn walk-comment
  [all-comments c-node]
  (if (map? c-node)
    (assoc c-node :children (comments-for-parent-id (:id c-node) all-comments))
    c-node))

#_(clojure.walk/prewalk (partial walk-comment test-comments) test-comments)

(reg-sub
 :current-story-nested-comments
 (fn [db _]
   (let [current-story (detail-story db)
         current-story-id (:id current-story)
         comments (vals (:comments db))
         root-comments (into #{} (map :id (filter #(= (:parent-id %) current-story-id) comments)))]
     (filter #(root-comments (:id %)) comments))))
