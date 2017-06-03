(ns hackernews.scenes.detail-view.views
  (:require [reagent.core :as r]
            [hackernews.components.react-native.core :as rn]
            [hackernews.components.story-row :as sr]
            [hackernews.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))

(defn- on-header-press
  [story-id]
  (dispatch [:open-story-external story-id]))

(defn story-header
  [story]
  [rn/touchable-highlight {:on-press #(on-header-press (:id story))}
   (sr/story-row {:story story})])

(defn child-comment
  [{:keys [id user time-ago content]}]
  [rn/view {:style {:padding 15}}
   [rn/text {:style {:color "#f26522" :margin-bottom 10}} (str user " " time-ago)]
   [rn/html-view {:value content :stylesheet {:p {:font-size 18}}}]])

(defn comment-row
  [{:keys [id user time-ago content] :as s} ]
  (let [child-comments (subscribe [:comments-with-parent-id id])]
    [rn/view {:style {:padding 15}}
    [rn/text {:style {:color "#f26522" :margin-bottom 10}} (str user " " time-ago)]
    [rn/html-view {:value content :stylesheet {:p {:font-size 18}}}]
     (map child-comment @child-comments)]))

(defn detail-view
  []
  (let [detail-story (subscribe [:detail-story])
        comments (subscribe [:current-story-parent-comments])]
     [l/list-view {::l/items (or @comments [])
                   ::l/header (story-header @detail-story)
                   ::l/render-row  comment-row}]))
