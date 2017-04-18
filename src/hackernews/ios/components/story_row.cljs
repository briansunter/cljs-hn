(ns hackernews.ios.components.story-row
  (:require [cljs.spec :as s]
            [hackernews.db :as db]
            [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(s/def ::on-press (s/fspec :args nil))
(s/def ::story-row-props (s/keys :req [::on-press]))

(s/fdef story-row
  :args (s/cat :story ::db/story :story-row-props ::story-row-props))

(defn story-row
  [story {:keys [on-press]}]
  [touchable-highlight {:on-press on-press}
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
                    :padding-top 2
                    :flex 1}}
      [view [text {:style {:font-size 12 :color "gray" :margin 2}} (str "by " (:user story))]]
      [view [text {:style {:font-size 12 :color "gray" :margin 2}} (:time_ago story)]]
      [view [text {:style {:font-size 12 :color "gray" :margin 2}} "|"]]
      [view [text {:style {:font-size 12 :color "gray" :margin 2}} (str (:comments_count story) " comments")]]]]]])
