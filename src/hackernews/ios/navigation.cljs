(ns hackernews.ios.navigation
  (:require [hackernews.shared.react-native.core :as rn]
            [reagent.core :as r]
            [cljs.spec :as s]
            [clojure.set :refer [rename-keys]]
            [hackernews.shared.scenes.detail-view.detail-view :as sd]
            [reagent.core :as r]))

(s/def ::component identity)
(s/def ::title string?)
(s/def ::pass-props map?)

(s/def ::route (s/keys :req [::component ::title ::pass-props]))

(s/fdef push-route!
        :args (s/cat :nav identity :route ::route))

(defonce ^:private navigator (atom nil))

(defn- push!
  [nav route]
  (.push nav route))

(defn push-route!
  [route]
  (when-not @navigator (throw (js/Error. "navigator not set up")))
  (-> (rename-keys route {::pass-props :passProps})
      (update ::component r/reactify-component)
      (clj->js)
      ((partial push! @navigator))))

(defn- update-navigator!
  [component]
  (->> (r/props component)
       :navigator
       (reset! navigator)))

(defn- navigation-container
  [content]
  (r/create-class
   {:render content
    :component-did-mount update-navigator!
    :component-did-update update-navigator!}))

(defn- wrap-navigation-container
  [route]
  (let [props (::pass-props props)]
    (update route ::component #(-> % navigation-container r/reactify-component))))

(defn push-story-detail-route!
  [story-id]
  (push-route! (wrap-navigation-container {::title "Story Detail"
                                           ::component sd/detail-view
                                           ::pass-props {:id story-id}})))

(defn navigation-root
  [initial-route]
  [rn/navigator
   {:initial-route (clj->js (wrap-navigation-container initial-route))
    :interactive-pop-gesture-enabled true
    :navigation-bar-hidden true
    :style {:position "absolute"
            :top 0
            :left 0
            :bottom 0
            :right 0}}])
