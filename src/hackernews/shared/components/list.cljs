(ns hackernews.shared.components.list
  (:require [cljs.spec :as s]
            [reagent.core :as r]
            [hackernews.db :as db]
            [hackernews.shared.react-native.core :as rn]))

(defn- row-separator
  [section-id row-id]
  [rn/view {:key (str section-id "-" row-id)
            :style {:height 0.5
                    :background-color "#efefef"}}])

(s/def ::item (s/keys :req-un [::db/id]))
(s/def ::items (s/coll-of ::item))
(s/def ::render-row (s/fspec :args (s/cat :item ::item)))
(s/def ::on-end-reached (s/fspec :args nil))
(s/def ::on-press (s/fspec :args (s/cat :id ::db/id)))
(s/def ::list-view-props (s/keys :req [::items ::render-row] :opt [::on-press ::on-end-reached]))
(s/fdef list-view :args (s/cat :props ::list-view-props))

(defn list-view
  [{:keys [::items ::render-row ::on-press ::on-end-reached]}]
  (let [ds (rn/ReactNative.ListView.DataSource. #js{:rowHasChanged (fn[a b] false)})]
    [rn/list-view {:dataSource (.cloneWithRows ds (clj->js items))
                   :render-row (fn [js-item]
                                 (let [item (js->clj js-item :keywordize-keys true)]
                                   (r/as-element
                                    [rn/touchable-highlight {:on-press #(on-press (:id item))
                                                             :key (:id item)}
                                     (render-row item)])))
                   :renderSeparator (fn [section-id row-id]
                                      (r/as-element
                                       [row-separator section-id row-id]))
                   :on-end-reached on-end-reached
                   :onEndReachedNumberThreshold 500}]))
