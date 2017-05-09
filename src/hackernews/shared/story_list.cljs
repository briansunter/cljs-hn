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
  []
  (let [stories (subscribe [:get-front-page-stories])
        ds (rn/ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)})]
    (fn []
      [rn/list-view {:dataSource (.cloneWithRows ds (clj->js @stories))
                     :render-row (fn [js-story]
                                   (let [story (js->clj js-story :keywordize-keys true)]
                                     (r/as-element
                                      [sr/story-row story {::sr/on-press #(dispatch [:open-story-external (:id story)])}])))
                     :renderSeparator (fn [section-id row-id]
                                        (r/as-element
                                         [row-separator section-id row-id]))
                     :on-end-reached (fn [] (dispatch [:load-front-page-stories]))
                     :onEndReachedNumberThreshold 500}])))
