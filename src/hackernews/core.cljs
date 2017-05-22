(ns hackernews.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.navigation :as ios-nav]
            [hackernews.ui.scenes.front-page.core :as fp]
            [hackernews.ui.components.react-native.core :as rn]))

(def initial-route
  {::ios-nav/title "Front Page"
   ::ios-nav/component fp/front-page})

(defn app-root []
  (fn []
    [ios-nav/navigation-root]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent rn/app-registry "hackernews" #(r/reactify-component app-root)))
