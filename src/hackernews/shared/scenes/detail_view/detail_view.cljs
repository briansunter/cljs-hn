(ns hackernews.shared.scenes.detail-view.detail-view
  (:require [reagent.core :as r]
            [cljs.spec :as s]
            [hackernews.db :as db]
            [clojure.walk :as w]
            [hackernews.shared.react-native.core :as rn]
            [hackernews.shared.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))


(defn comment-row
  [comment]
  [rn/view {:style {:padding 10}}[rn/text comment]])

(defn collect-comments
  [comment]
  (->> comment
  (tree-seq map? :comments)
  (map :content)))

(defn detail-view
  [{:keys [id]}]
  (let [story (subscribe [:get-story id])]
    (fn []
      [rn/view
       {:style {:padding-top 10}}
       [sr/story-row @story]
       [l/list-view {::l/items (collect-comments @story)
                     ::l/on-press #()
                     ::l/render-row  comment-row
                     ::l/on-end-reached #()}]])))
