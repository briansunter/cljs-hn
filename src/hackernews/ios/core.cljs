(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.ios.navigation :as ios-nav]
            [hackernews.shared.scenes.front-page.front-page :as fp]
            [hackernews.shared.react-native.core :as rn]))

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
