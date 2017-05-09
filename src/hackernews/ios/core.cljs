(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.shared.story-list :as sl]
            [hackernews.shared.react-native.core :as rn]))

(defn app-root []
  (fn []
    [rn/navigator
     {:initial-route {:title "Front Page" :component (r/reactify-component sl/story-scene)}
      :interactive-pop-gesture-enabled true
      :navigation-bar-hidden true
      :style {:position"absolute"
              :top 0
              :left 0
              :bottom 0
              :right 0}}]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent rn/app-registry "hackernews" #(r/reactify-component app-root)))
