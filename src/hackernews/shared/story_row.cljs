(ns hackernews.shared.story-row
  (:require [cljs.spec :as s]
            [hackernews.db :as db]
            [reagent.core :as r]
            [hackernews.shared.react-native.core :as rn]))

(defn- points-view
  [points]
  [rn/view {:style {:flex 1
                    :padding 5
                    :align-items "center"
                    :justify-content "center"}}
   [rn/text {:number-of-lines 1
             :adjust-font-size-to-fit true
             :minimum-font-scale .2
             :style {:font-size 20
                     :font-weight "bold"
                     :color "#f26522"}}
    points]])

(defn- subtitle-view
  [s]
  [rn/view [rn/text {:style {:font-size 12 :color "gray" :margin 2}} s]])

(defn- detail-view
  [{:keys [user time_ago comments_count]}]
  [rn/view {:style {:flex-direction "row"
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
  [{:keys [id points title read?] :as story} {:keys [::on-press]}]
  [rn/touchable-highlight {:on-press on-press
                           :key id}
   [rn/view {:style {:flex-direction "row"
                     :margin 10}}
    [points-view points]
    [rn/view {:style {:flex 7
                      :flex-direction "column"
                      :justify-content "center"
                      :align-items "flex-start"}}
     [rn/text {:key title
               :style {:font-size 20
                       :color (if read? "grey" "black")}}
      title]
     [detail-view story]]]])
