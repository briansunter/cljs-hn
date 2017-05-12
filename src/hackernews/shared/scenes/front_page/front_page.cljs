(ns hackernews.shared.scenes.front-page.front-page
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [hackernews.shared.scenes.front-page.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [hackernews.shared.react-native.core :as rn]))

(defn on-press
  [story-id]
  (dispatch [:read-story story-id])
  (dispatch [:open-story-external story-id]))

(defn story-list
  [{:keys [stories]}]
  [l/list-view {::l/items stories
                ::l/on-press on-press
                ::l/render-row [sr/story-row]
                ::l/on-end-reached #(dispatch [:load-front-page-stories])}])

(defn front-page
  [{:keys [navigator]}]
  (let [stories (subscribe [:get-front-page-stories])]
    [story-list {:stories @stories}]))
