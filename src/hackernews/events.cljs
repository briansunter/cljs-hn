(ns hackernews.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
   [hackernews.api :as api]
   [hackernews.utils :refer [dec-to-zero]]
   [hackernews.navigation :as nav]
   [hackernews.interceptors :as i]
   [hackernews.db :as db :refer [app-db]]))

(reg-event-db
 :initialize-db
 i/interceptors
 (fn [_ _]
   app-db))

(reg-fx
 :fetch-http
 (fn [{:as   request
       :keys [url params method on-success on-failure]
       :or   {on-success      [:http-no-on-success]
              on-failure      [:http-no-on-failure]}}]
   (api/fetch (assoc request
                     :on-success #(dispatch (conj on-success %))
                     :on-failure #(dispatch (conj on-failure %))))))

(reg-event-db
 :pop-stack-nav
 i/interceptors
 (fn [db _]
   (-> (update-in db [:navigation :router-state :index] dec-to-zero)
       (update-in [:navigation :router-state :routes] pop))))

#_(reg-event-db
 :push-stack-nav
 i/interceptors
 (fn [db [_ route-name params]]
   (nav/push-nav-stack db route-name params)))

(reg-event-fx
 :nav/js
 i/interceptors
 (fn [{:keys [db]} [_ type route-name params]]
   (js/console.log "JS NAV" type route-name)
   {:dispatch (case type
                "Navigation/BACK" [:pop-stack-nav]
                "Navigation/NAVIGATE" [])
    :db       db}))
