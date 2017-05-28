(ns hackernews.navigation
  (:require [hackernews.components.react-native.core :as rn]
            [reagent.core :as r]
            [cljs.spec :as s]
            [clojure.set :refer [rename-keys]]
            [camel-snake-kebab.core :refer [->camelCase ->kebab-case]]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.scenes.front-page.views :as fp]
            [hackernews.components.story-row :as sr]
            [hackernews.scenes.detail-view.views :as sd]))

(def routes {:front-page {:screen (r/reactify-component fp/front-page)
                          :navigationOptions {:title "Front Page"}}
             :story-detail {:screen (r/reactify-component sd/detail-view)
                            :navigationOptions (fn [_] (clj->js {:title (:title @(subscribe [:detail-story]))
                                                                 :headerTitleStyle {:fontSize 12 :fontWeight :bold
                                                                                    :numberOfLines 2}}))}})

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
