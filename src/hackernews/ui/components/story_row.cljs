(ns hackernews.ui.components.story-row
  (:require [cljs.spec :as s]
            [hackernews.db :as db]
            [reagent.core :as r]
            [hackernews.ui.components.react-native.core :as rn]))

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

(s/def ::number-of-lines pos-int?)
(s/def ::story-row-props (s/keys :req-un [::db/story] :opt-un [::number-of-lines]))

(s/fdef story-row
        :args (s/cat :props ::story-row-props))

(defn story-row
  [{:keys [story number-of-lines]}]
  (let [{:keys [id points title read?]} story]
    [rn/view {:style {:flex-direction "row"
                      :padding 10}}
     [points-view points]
     [rn/view {:style {:flex 7
                       :flex-direction "column"
                       :align-items "flex-start"}}
      [rn/text (merge {:key title
                 :style {:font-size 20
                         :color (if read? "grey" "black")}}
                      (when number-of-lines {:number-of-lines number-of-lines}))
       title]
      [detail-view story]]]))
