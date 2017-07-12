(ns hackernews.scenes.detail-view.views
  (:require [reagent.core :as r]
            [hackernews.components.react-native.core :as rn]
            [hackernews.components.story-row :as sr]
            [hackernews.components.list :as l]
            [re-frame.core :refer [subscribe dispatch]]))

(defn share-story-button
  [story-id]
  [rn/button {:title "Share" :on-press #(dispatch [:share-story story-id])}])

(defn navigation-options
  []
  (let [story (subscribe [:detail-story])]
    (clj->js {:title (:title @story)
              :headerRight (r/as-element (share-story-button (:id @story)))
              :headerTitleStyle {:fontSize 12 :fontWeight :bold}})))

(defn- on-header-press
  [story-id]
  (dispatch [:open-story-external story-id]))

(defn story-header
  [story]
  [rn/touchable-highlight {:on-press #(on-header-press (:id story))}
   (sr/story-row {:story story})])

(defn child-comments
  [{:keys [id user time-ago content]}]
  (let [comments (subscribe [:comments-with-parent-id id])]
    [rn/view {:style {:flex-direction "row"}}
     [rn/view
      {:flex 1
       :style {:background-color "orange"
               :margin-top 10}}]
     [rn/view {:flex 150
               :style {:padding-left 15
                       :padding-top 15
                       :padding-bottom 15} :key id}
      [rn/text {:style {:color "#f26522" :margin-bottom 10}} (str user " " time-ago)]
      [rn/html-view {:value content}]
      [rn/view (for [c @comments]
                 ^{:key (:id c)}
                 [child-comments c])]]]))

  (defn comment-row
    [{:keys [id user time-ago content] :as s}]
    (fn []
      [rn/view {:key id :style {:padding-right 15}} [child-comments s]]))

(defn detail-view
  []
  (let [detail-story (subscribe [:detail-story])
        comments (subscribe [:current-story-nested-comments])]
    [rn/view
     [l/list-view {::l/items (or @comments [])
                   ::l/header (story-header @detail-story)
                   ::l/render-row  comment-row}] ]))
