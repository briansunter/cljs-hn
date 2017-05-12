(ns hackernews.shared.scenes.front-page.front-page
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.shared.components.story-row :as sr]
            [hackernews.shared.components.list :as l]
            [hackernews.shared.scenes.detail-view.detail-view :as d]
            [hackernews.shared.react-native.core :as rn]))

(defn navigate
  [nav title comp story-id]
  (.push nav #js{:title title :component (r/reactify-component comp)
                 :passProps #js{:id story-id}}))

(defn- on-press
  [nav story-id]
  (dispatch [:read-story story-id])
  (dispatch [:load-story-comments story-id])
  (navigate nav "Detail" d/detail-view story-id)
  #_(dispatch [:open-story-external story-id]))

(defn front-page
  [{:keys [navigator]}]
  [l/list-view {::l/items @(subscribe [:get-front-page-stories])
                ::l/on-press (partial on-press navigator)
                ::l/render-row sr/story-row
                ::l/on-end-reached #(dispatch [:load-front-page-stories])}])
