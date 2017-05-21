(ns hackernews.ios.navigation
  (:require [hackernews.shared.react-native.core :as rn]
            [reagent.core :as r]
            [cljs.spec :as s]
            [clojure.set :refer [rename-keys]]
            [camel-snake-kebab.core :refer [->camelCase ->kebab-case]]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.shared.scenes.detail-view.detail-view :as sd]))

(s/def ::component identity)
(s/def ::title string?)
(s/def ::pass-props map?)

(s/def ::route (s/keys :req [::component ::title] :opt [::pass-props]))

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
  (let [props (::pass-props route)]
    (update route ::component #(-> % navigation-container r/reactify-component))))

(defn push-story-detail-route!
  [story-id]
  (push-route! (wrap-navigation-container {::title "Story Detail"
                                           ::component sd/detail-view
                                           ::pass-props {:id story-id}})))

(defn foo
  []
  [rn/touchable-highlight {:on-press #(dispatch [:push-stack-nav "story-detail"])}[rn/text "foo"]])

(defn bar
  []
  [rn/view [rn/text "bar"]])

(def routes {:front-page {:screen (r/reactify-component foo)}
              :story-detail {:screen (r/reactify-component bar)}})

(def stack-navigator (rn/stack-navigator (clj->js routes)))

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
