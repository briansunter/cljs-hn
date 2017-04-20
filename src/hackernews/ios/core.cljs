(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.ios.components.story-row :as sr]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))

(defn- on-row-press
  [{:keys [story-id url]}]
  (dispatch [:read-story story-id]))

(defn- row-separator
  [section-id row-id]
  [view {:key (str section-id "-" row-id)
         :style {:height 0.5
                 :background-color "#efefef"}}])
(defn story-list
  []
  (let [stories (subscribe [:get-front-page-stories])
        ds (ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)})]
    (fn []
      [list-view {:dataSource (.cloneWithRows ds (clj->js @stories))
                  :render-row (fn [js-story]
                                (let [story (js->clj js-story :keywordize-keys true)]
                                  (r/as-element
                                   [sr/story-row story {::sr/on-press #(dispatch [:open-story-external (:id story)])}])))
                  :renderSeparator (fn [section-id row-id]
                                     (r/as-element
                                      [row-separator section-id row-id]))
                  :on-end-reached #(dispatch [:load-front-page-stories])
                  :onEndReachedNumberThreshold 500}])))

(defn app-root []
  (fn []
    [view {:style {:flex-direction "column"
                   :background-color "#f6f6ef"}}
     [story-list]]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent app-registry "hackernews" #(r/reactify-component app-root)))
