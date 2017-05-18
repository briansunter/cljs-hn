(ns hackernews.shared.components.story-row
  (:require [cljs.spec :as s]
            [hackernews.db :as db]
            [reagent.core :as r]
            [hackernews.shared.react-native.core :as rn]))

(defn- points-view
  [points]
  [rn/view {:style {:align-items "center"
                    :flex 1
                    :padding 10
                    :justify-content "center"}}
   [rn/text {:number-of-lines 1
             :style {:font-size 20
                     :font-weight "bold"
                     :color "#f26522"}}
    points]])

(defn- subtitle-view
  [s]
  [rn/view [rn/text {:style {:font-size 12 :color "gray" :margin 2}} s]])

(defn- detail-view
  [{:keys [user time_ago comments_count]}]
  [rn/view {:style {:flex-direction "row"}}
   [subtitle-view (str "by " user)]
   [subtitle-view time_ago]
   [subtitle-view "|"]
   [subtitle-view (str comments_count " comments")]])

(s/fdef story-row
        :args (s/cat :story ::db/story ))

(defn story-row
  [{:keys [id points title read?] :as story}]
   [rn/view {:style {:flex-direction "row"
                     :padding 10}}
    [points-view points]
    [rn/view {:style {:flex 7
                      :flex-direction "column"
                      :align-items "flex-start"}}
     [rn/text {:key title
               :number-of-lines 2
               :style {:font-size 20
                       :color (if read? "grey" "black")}}
      title]
     [detail-view story]]])
