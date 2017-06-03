(ns hackernews.components.list
  (:require [cljs.spec :as s]
            [reagent.core :as r]
            [hackernews.db :as db]
            [hackernews.components.react-native.core :as rn]))

(defn- row-separator
  [section-id row-id]
  [rn/view {:key (str section-id "-" row-id)
            :style {:height 0.5
                    :background-color "#efefef"}}])

(s/def ::item (s/keys :req-un [::db/id]))
(s/def ::items (s/coll-of ::item))
(s/def ::render-row (s/fspec :args (s/cat :item ::item)))
(s/def ::on-end-reached (s/fspec :args nil))
(s/def ::hiccup (s/or :vec vector? :fn fn?))
(s/def ::header ::hiccup)
(s/def ::list-view-props (s/keys :req [::items ::render-row] :opt [::on-end-reached ::header]))

(s/fdef list-view :args (s/cat :props ::list-view-props))

(defn list-view
  [{:keys [::items ::render-row ::header ::on-end-reached ]}]
  (let [ds (rn/ReactNative.ListView.DataSource. #js{:rowHasChanged (fn [a b] false)})]
    [rn/list-view {:dataSource (.cloneWithRows ds (clj->js items))
                   :content-container-style {:padding-bottom 100}
                   :render-header #(r/as-element header)
                   :render-row (fn [js-item]
                                 (let [item (js->clj js-item :keywordize-keys true)]
                                   (r/as-element
                                    (render-row item))))
                   :render-separator (fn [section-id row-id]
                                      (r/as-element
                                       [row-separator section-id row-id]))
                   :on-end-reached on-end-reached
                   :onEndReachedNumberThreshold 500}]))
