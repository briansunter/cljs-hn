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
(defn foo
  []
  [rn/touchable-highlight {:on-press #(dispatch [:push-stack-nav "story-detail"])} [rn/text "foo"]])

(defn bar
  [_]
  [rn/view [rn/text "barasdfkajskldjflksjdflkjasdlkfjlskadjflkajdslkfjaslkdjfklasjdflk"]])

(def routes {:front-page {:screen (r/reactify-component fp/front-page)}
             :story-detail {:screen (r/reactify-component sd/detail-view)}})

(defn bas
  []
  [sr/story-row {:title "Man Bites dog" :points 100}])

(def stack-navigator (rn/stack-navigator (clj->js routes) (clj->js {:navigationOptions {:headerTintColor "orange"
                                                                                        :header (r/create-element (r/reactify-component foo))
                                                                                        }})))

#_(defn navigation-action
    [a]
    (.getActionForPathAndParams (.-router stack-navigator) a))

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
