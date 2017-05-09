(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.shared.story-list :as sl]
            [hackernews.shared.react-native.core :as rn]
            ))

(defn app-root []
  (fn []
    [rn/view {:style {:flex-direction "column"
                   :background-color "#f6f6ef"}}
     [sl/story-list]]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent rn/app-registry "hackernews" #(r/reactify-component app-root)))
