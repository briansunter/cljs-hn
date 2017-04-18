(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]
            [hackernews.ios.components.story-row :as sr]
            ))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))
(def ds (ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)}))
(def linking (.-Linking ReactNative))

(defn open-url! [url]
  (.openURL linking url))

(defn on-row-press
  []
  (dispatch [:read-story (:id story)])
  (open-url! (:url story)))

(defn row->story-row
  [row]
  [sr/story-row (js->clj row :keywordize-keys true) on-row-press])

(defn story-list
  []
  (let [stories (subscribe [:get-front-page-stories])]
    (fn []
      [list-view {:dataSource (.cloneWithRows ds (clj->js @stories))
                  :render-row (fn[row]
                                (r/as-element
                                 [view (row->story-row row)]))
                  :renderSeparator (fn [section-id row-id]
                                     (r/as-element
                                      [view {:key (str section-id "-" row-id)
                                             :style {:height 0.5
                                                     :background-color "#efefef"}}]))
                  :on-end-reached (fn [_] (dispatch [:load-front-page-stories]))
                  :onEndReachedNumberThreshold 500
                  :style nil}])))

(defn app-root []
  (fn []
    [view {:style {:flex-direction "column"
                   :background-color "#f6f6ef"}}
     [story-list]]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent app-registry "hackernews" #(r/reactify-component app-root)))
