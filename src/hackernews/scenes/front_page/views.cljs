(ns hackernews.scenes.front-page.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.components.story-row :as sr]
            [hackernews.components.list :as l]
            [hackernews.components.react-native.core :as rn]))

(defn- on-press
  [story-id]
  (dispatch [:nav-story-detail story-id]))

(defn- front-page-row
  [{:keys [on-press]} story]
  [rn/touchable-highlight {:on-press #(on-press (:id story))
                           :key (:id story)}
   (sr/story-row {:story story})])

(defn front-page
  [{:keys [navigation]}]
  [l/list-view {::l/items (or @(subscribe [:get-front-page-stories]) [])
                ::l/render-row (partial front-page-row {:on-press on-press})
                ::l/on-end-reached #(dispatch [:load-front-page-stories])}])
