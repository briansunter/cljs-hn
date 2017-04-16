(ns hackernews.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [day8.re-frame.http-fx]
            [hackernews.events]
            [hackernews.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))
(def ds (ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)}))
(def linking (.-Linking ReactNative))

(defn open-url! [url]
  (.openURL linking url))

(def logo-img (js/require "./images/cljs.png"))

(def row-comp (r/reactify-component (fn[props]
                                      (let [row (props :row)]
                                        [touchable-highlight {:style {:border-top-width 2
                                                                      :border-color "#000"}}
                                         [text @row]]))))
(defn story-row
  [story]
  [touchable-highlight {:on-press #(do
                                     (dispatch [:read-story (:id story)])
                                     (open-url! (:url story)))}
   [view {:style {:flex-direction "row"
                  :margin 10}}
    [view {:style {:flex 1
                   :padding 5
                   :align-items "center"
                   :justify-content "center"}}
     [text {:style {:font-size 20
                    :font-weight "bold"
                    :color "#f26522"}}
      (str (:points story))]]
    [view {:style {:flex 7
                   :flex-direction "column"
                   :justify-content "center"
                   :align-items "flex-start"}}
     [text {:style {:font-size 20
                    :color (if (:read? story) "gray" "black")}} (str (:title story))]
     [view {:style {:flex-direction "row"
                    :justify-content "space-between"
                    :flex 1}}
      [view [text {:style {:font-size 12 :color "gray"}} (str "by " (:user story))]]
      [view [text {:style {:font-size 12 :color "gray"}} (:time_ago story)]]
      [view [text {:style {:font-size 12 :color "gray"}} "|"]]
      [view [text {:style {:font-size 12 :color "gray"}} (str (:comments_count story) " comments")]]]]] ])



(defn story-list
  []
  (let [stories (subscribe [:get-front-page-stories])]
    (fn []
      [list-view {:dataSource (.cloneWithRows ds (clj->js @stories))
                  :render-row (fn[row]
                                (r/as-element
                                 [view [story-row (js->clj row :keywordize-keys true)]]))
                  :renderSeparator (fn [section-id row-id]
                                     (r/as-element
                                      [view {:key (str section-id "-" row-id)
                                             :style {:height 0.5
                                                     :background-color "#efefef"}}]))
                  :on-end-reached (fn [_] (dispatch [:load-front-page-stories]))
                  :onEndReachedNumberThreshold 500
                  :style nil}])))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column"
                     :background-color "#f6f6ef"}}
       [story-list]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:load-front-page-stories])
  (.registerComponent app-registry "hackernews" #(r/reactify-component app-root)))
