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
  [rn/view {:style {:margin 10}}
   [rn/text {:style {:padding 5
                     :color "#f26522"}}
    (str (:user comment) " " (:time_ago comment))]
   [rn/html-view {:value (:content comment)
                  :style {:flex 1}
                  :stylesheet {:p {:font-size 18}}}]])

(defn collect-comments
  [comment]
  (->>
   (tree-seq map? :comments comment)
   (filter :content)))

(defn detail-view
  [{:keys [id]}]
  (let [story (subscribe [:get-story id])]
    (fn []
      [rn/view
       {:style {:padding-top 10}}
       [sr/story-row @story]
       [l/list-view {::l/items (collect-comments @story)
                     ::l/render-row  comment-row}]])))
