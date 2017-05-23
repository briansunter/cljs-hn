(ns hackernews.navigation
  (:require [hackernews.ui.components.react-native.core :as rn]
            [reagent.core :as r]
            [cljs.spec :as s]
            [clojure.set :refer [rename-keys]]
            [camel-snake-kebab.core :refer [->camelCase ->kebab-case]]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.ui.scenes.front-page.core :as fp]
            [hackernews.ui.components.story-row :as sr]
            [hackernews.ui.scenes.detail-view.core :as sd]))

#_(defn story-detail-header
    []
    (let [story (subscribe [:detail-story])]

      [rn/view {:style {:padding-top 20
                        :padding 10
                        :justify-content "center"
                        :align-items "center"
                        :flex-direction "row"
                        :height 60}}
       [rn/view  [rn/touchable-opacity {:on-press #(dispatch [:pop-stack-nav])
                                        }
                  [rn/text "back"]]]
       [rn/view {
                 :style {:justify-content "center"
                         :flex 1
                         :padding 10}}
        [rn/text {:number-of-lines 1
                  :style {:color "#f26522"
                          :font-weight "bold"}}(:points @story)]]

       [rn/view {:flex 8}[rn/text (:title @story)]]]))

(def routes {:front-page {:screen (r/reactify-component fp/front-page)
                          :navigationOptions {:title "Front Page"}}
             :story-detail {:screen (r/reactify-component sd/detail-view)
                            :navigationOptions (fn [_] (clj->js {:title (:title @(subscribe [:detail-story]))
                                                                 :headerTitleStyle {:fontSize 12 :fontWeight :bold
                                                                                    :numberOfLines 2}
                                                                 }))}})


(def stack-navigator (rn/stack-navigator (clj->js routes)))

(defn update-keys
  [m f]
  (into {} (map (fn [[k v]] {(f k) v}) m)))

(defn format-nav-state
  [ns]
  (clj->js (update ns :routes (fn [rs] (map #(update-keys % ->camelCase) rs)))))

(defn dispatch-nav
  [nav-val]
  (let [type (.-type nav-val)
        route-name (.-routeName nav-val)
        params (.-params nav-val)]
    (dispatch [:nav/js type route-name params])))

(defn navigation-root
  []
  (let [nav-state (subscribe [:nav-state])]
    (.log js/console (format-nav-state @nav-state))
    [(r/adapt-react-class stack-navigator) {:navigation (rn/add-navigation-helpers
                                                         (clj->js
                                                          {"state" (format-nav-state @nav-state)
                                                           "dispatch" #(dispatch-nav %)}))}]))
