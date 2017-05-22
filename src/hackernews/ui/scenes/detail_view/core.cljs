(ns hackernews.ui.scenes.detail-view.core
  (:require [reagent.core :as r]
            [hackernews.ui.components.react-native.core :as rn]
            [hackernews.ui.components.story-row :as sr]
            [hackernews.ui.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))

(defn- on-header-press
  [story-id]
  (dispatch [:open-story-external story-id]))

(defn story-header
  [story]
  [rn/touchable-highlight {:on-press #(on-header-press (:id story))}
   (sr/story-row story)])

(defn comment-row
  [{:keys [user time_ago content]}]
  [rn/view {:style {:padding 15}}
   [rn/text {:style {:color "#f26522" :margin-bottom 10}} (str user " " time_ago)]
   [rn/html-view {:value content :stylesheet {:p {:font-size 18}}}]])

(defn detail-view
  []
  (let [detail-story (subscribe [:detail-story])
        comments (subscribe [:current-story-flat-comments])]
    [rn/view {:style {:margin-top 10}}
     [story-header @detail-story]
     [l/list-view {::l/items @comments
                   ::l/render-row  comment-row}]]))
