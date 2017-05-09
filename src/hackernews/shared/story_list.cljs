(ns hackernews.shared.story-list
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [hackernews.shared.story-row :as sr]
            [hackernews.shared.react-native.core :as rn]))

(defn- on-row-press
  [{:keys [story-id url]}]
  (dispatch [:read-story story-id]))

(defn- row-separator
  [section-id row-id]
  [rn/view {:key (str section-id "-" row-id)
            :style {:height 0.5
                    :background-color "#efefef"}}])

(defn story-list
  [{:keys [stories on-press on-end-reached]}]
  (let [ds (rn/ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)})]
    [rn/list-view {:dataSource (.cloneWithRows ds (clj->js stories))
                   :render-row (fn [js-story]
                                 (let [story (js->clj js-story :keywordize-keys true)]
                                   (r/as-element
                                    [sr/story-row story {::sr/on-press (on-press (:id story))}])))
                   :renderSeparator (fn [section-id row-id]
                                      (r/as-element
                                       [row-separator section-id row-id]))
                   :on-end-reached on-end-reached
                   :onEndReachedNumberThreshold 500}]))

(defn story-scene
  [{:keys [navigator]}]
  (let [stories (subscribe [:get-front-page-stories])
        on-press (fn [story-id] #(dispatch [:open-story-external story-id]))
        on-end-reached #(dispatch [:load-front-page-stories])]
    [story-list {:stories @stories
                 :on-press on-press
                 :on-end-reached on-end-reached}]))
