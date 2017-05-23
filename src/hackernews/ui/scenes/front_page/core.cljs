(ns hackernews.ui.scenes.front-page.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.ui.components.story-row :as sr]
            [hackernews.ui.components.list :as l]
            [hackernews.ui.scenes.detail-view.core :as d]
            [hackernews.ui.components.react-native.core :as rn]))

(defn- on-press
  [story-id]
  (dispatch [:nav-story-detail story-id]))

(defn- front-page-row
  [{:keys [on-press]} story]
  [rn/touchable-highlight {:on-press #(on-press (:id story))
                           :key (:id story)}
   (sr/story-row {:story story})])

(defn front-page
  [{:keys [navigator]}]
  [l/list-view {::l/items @(subscribe [:get-front-page-stories])
                ::l/render-row (partial front-page-row {:on-press (partial on-press )})
                ::l/on-end-reached #(dispatch [:load-front-page-stories])}])
