(ns hackernews.navigation
  (:require [hackernews.components.react-native.core :as rn]
            [hackernews.db :as db]
            [reagent.core :as r]
            [cljs.spec :as s]
            [clojure.set :refer [rename-keys]]
            [camel-snake-kebab.core :refer [->camelCase ->kebab-case]]
            [re-frame.core :refer [subscribe dispatch]]
            [hackernews.scenes.front-page.views :as fp]
            [hackernews.utils :refer [update-keys]]
            [hackernews.components.story-row :as sr]
            [hackernews.scenes.detail-view.views :as sd]))

(def routes {:front-page {:screen (r/reactify-component fp/front-page)
                          :navigationOptions fp/navigation-options}
             :story-detail {:screen (r/reactify-component sd/detail-view)
                            :navigationOptions sd/navigation-options}})

(def stack-navigator (rn/stack-navigator (clj->js routes)))

(defn current-route
  [db]
  (let [{:keys [routes index]} (get-in db [:navigation :router-state])]
    (nth routes index)))

(defn format-nav-state
  [ns]
  (clj->js (update ns :routes (fn [rs] (map #(update-keys ->camelCase %) rs)))))

(defn dispatch-nav
  [nav-val]
  (let [type (.-type nav-val)
        route-name (.-routeName nav-val)
        params (.-params nav-val)]
    (dispatch [:nav/js type route-name params])))

(s/fdef push-nav-stack
  :args (s/cat :db ::db/navigation
               :route-name ::db/route-name
               :params ::db/params)
  :ret ::db/navigation)

(defn push-nav-stack
  [db route-name params]
  (let [current-routes (get-in db [:router-state :routes])
        last-route (last current-routes)
        next-route {:route-name :story-detail :params params}]
    (if (= (:route-name last-route) (:route-name next-route))
      db
      (-> (update-in db [:router-state :index] inc)
          (update-in [:router-state :routes] #(conj % next-route))))))

(defn navigation-root
  []
  (let [nav-state (subscribe [:nav-state])]
    [(r/adapt-react-class stack-navigator) {:navigation (rn/add-navigation-helpers
                                                         (clj->js
                                                          {"state" (format-nav-state @nav-state)
                                                           "dispatch" #(dispatch-nav %)}))}]))
