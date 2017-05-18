(ns hackernews.shared.scenes.detail-view.detail-view
  (:require [reagent.core :as r]
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
                  :stylesheet {:p {:font-size 18}}}]])

(defn detail-view
  [{:keys [id]}]
  [rn/view
   {:style {:padding-top 10}}
   [sr/story-row @(subscribe [:get-story id])]
   [l/list-view {::l/items @(subscribe [:story-flat-comments id])
                 ::l/render-row  comment-row}]])
