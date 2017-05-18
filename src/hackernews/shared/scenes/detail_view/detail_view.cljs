(ns hackernews.shared.scenes.detail-view.detail-view
  (:require [reagent.core :as r]
            [hackernews.shared.react-native.core :as rn]
            [hackernews.shared.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))

(defn comment-row
  [{:keys [user time_ago content]}]
  [rn/view {:style {:padding 15}}
   [rn/text {:style {:color "#f26522"
                     :margin-bottom 10}}
    (str user " " time_ago)]
   [rn/html-view {:value content
                  :stylesheet {:p {:font-size 18}}}]])

(defn detail-view
  [{:keys [id]}]
  [rn/view
   {:style {:margin-top 10}}
   [sr/story-row @(subscribe [:get-story id])]
   [l/list-view {::l/items @(subscribe [:story-flat-comments id])
                 ::l/render-row  comment-row}]])
