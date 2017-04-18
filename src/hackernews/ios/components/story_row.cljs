(ns hackernews.ios.components.story-row
  (:require [cljs.spec :as s]
            [hackernews.db :as db]
            [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(defn- points-view
  [points]
  [view {:style {:flex 1
                 :padding 5
                 :align-items "center"
                 :justify-content "center"}}
   [text {:style {:font-size 20
                  :font-weight "bold"
                  :color "#f26522"}}
    points]])

(defn- subtitle-view
  [s]
  [view [text {:style {:font-size 12 :color "gray" :margin 2}} s]])

(defn- detail-view
  [{:keys [user time_ago comments_count]}]
  [view {:style {:flex-direction "row"
                 :padding-top 2
                 :flex 1}}
   [subtitle-view (str "by " user)]
   [subtitle-view time_ago]
   [subtitle-view "|"]
   [subtitle-view (str comments_count " comments")]])

(s/def ::on-press (s/fspec :args nil))
(s/def ::story-row-props (s/keys :req [::on-press]))

(s/fdef story-row
  :args (s/cat :story ::db/story :story-row-props ::story-row-props))

(defn story-row
  [{:keys [points title read?] :as story} {:keys [on-press]}]
  [touchable-highlight {:on-press on-press}
   [view {:style {:flex-direction "row"
                  :margin 10}}
    [points-view points]
    [view {:style {:flex 7
                   :flex-direction "column"
                   :justify-content "center"
                   :align-items "flex-start"}}
     [text {:style {:font-size 20
                    :color (if read "gray" "black")}}
      title]
     [detail-view story]]]])
