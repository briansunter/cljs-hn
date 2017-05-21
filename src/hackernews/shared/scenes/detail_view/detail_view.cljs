(ns hackernews.shared.scenes.detail-view.detail-view
  (:require [reagent.core :as r]
            [hackernews.shared.react-native.core :as rn]
            [hackernews.shared.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))

(defn story-header
  [story]
  [rn/touchable-highlight {:on-press #(dispatch [:open-story-external (:id story)])}
   (sr/story-row story)])

(defn comment-row
  [{:keys [user time_ago content]}]
  [rn/view {:style {:padding 15}}
   [rn/text {:style {:color "#f26522"
                     :margin-bottom 10}}
    (str user " " time_ago)]
   [rn/html-view {:value content
                  :stylesheet {:p {:font-size 18}}}]])

(defn detail-view
  [_]
  (let [current-story (subscribe [:current-story])
        comments (subscribe [:current-story-flat-comments])]
      [rn/view
      {:style {:margin-top 10}}
      [story-header @current-story]
      [l/list-view {::l/items @comments
                    ::l/render-row  comment-row}]]))
