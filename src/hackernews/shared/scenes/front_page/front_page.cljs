(ns hackernews.shared.scenes.front-page.front-page
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.shared.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [hackernews.shared.scenes.detail-view.detail-view :as d]
            [hackernews.shared.react-native.core :as rn]))

(defn- on-press
  [nav story-id]
  (dispatch [:nav-story-detail story-id]))

(defn- front-page-row
  [{:keys [on-press]} story]
  [rn/touchable-highlight {:on-press #(on-press (:id story))
                           :key (:id story)}
   (sr/story-row story)])

(defn front-page
  [{:keys [navigator]}]
  [l/list-view {::l/items @(subscribe [:get-front-page-stories])
                ::l/render-row (partial front-page-row {:on-press (partial on-press navigator)})
                ::l/on-end-reached #(dispatch [:load-front-page-stories])}])
