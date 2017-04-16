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

(defn list-view-source [v]
  (let [res #js[]]
    (doseq [item v]
      (.push res (r/atom item)))
    (r/atom (js->clj res))))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(def row-comp (r/reactify-component (fn[props]
                                      (let [row (props :row)]
                                        [touchable-highlight {:style {:border-top-width 1 :border-color "#000"} :on-press #(alert (str @row))}
                                         [text @row]]))))

(defn story-list
  []
  (let [stories (subscribe [:get-front-page-stories])]
    (fn []
      [list-view {:dataSource (.cloneWithRows ds (clj->js @stories))
                  :render-row (fn[row]
                                (r/as-element
                                 [view [text (:title (js->clj row :keywordize-keys true))]]))
                  :renderSeparator (fn [section-id row-id]
                                     (r/as-element
                                      [view {:key (str section-id "-" row-id)
                                             :style {:height 0.5
                                                     :margin-left 10
                                                     :margin-right 10
                                                     :background-color "#efefef"}}]))
                  :on-end-reached (fn [_] (dispatch [:load-front-page-stories]))
                  :onEndReachedNumberThreshold 100
                  :style nil}]
      ;; #_[view
      ;;    (for [s @stories]
      ;;      ^{:key (:id s)}
      ;;      [view [text (:title s)]])]
      )))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(dispatch [:load-front-page-stories 0])}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]
       [story-list]
       ])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "hackernews" #(r/reactify-component app-root)))
