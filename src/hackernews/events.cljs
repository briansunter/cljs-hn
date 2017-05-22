(ns hackernews.events
  (:require
   [re-frame.core :refer [reg-event-db after reg-event-fx reg-cofx reg-fx]]
   [ajax.core :as ajax]
   [clojure.spec :as s]
   [hackernews.navigation :as ios-nav]
   [hackernews.ui.components.react-native.core :as rn]
   [hackernews.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;

(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

(def logging
  (after (fn [db [e]] (.log js/console "EVENT" e))))

(def interceptors [validate-spec logging])

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(defn story-with-id
  [story-id stories]
  (first (filter #(= story-id (:id %)) stories)))

(reg-event-db
 :read-story
 validate-spec
 (fn [db [_ story-id]]
   (let [stories (:stories db)
         updated-stories (map #(if (= story-id (:id %)) (assoc % :read? true) %) stories)]
     (assoc db :stories updated-stories))))

(reg-event-fx
 :loaded-front-page-stories
 validate-spec
 (fn [cofx [_ stories]]
   {:db (-> (update (:db cofx) :stories #(concat % stories))
            (update-in [:front-page :current-page-num] inc))
    }))

(reg-event-fx
 :failed-loading-front-page-stories
 validate-spec
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}
   ))

(reg-event-fx
 :loaded-story-comments
 validate-spec
 (fn [{:keys [db]} [_ {:keys [id comments]}]]
   (let [stories (:stories db)
         updated-stories (map #(if (= id (:id %)) (assoc % :comments comments) %) stories)]
   {:db (assoc db :stories updated-stories)})))

;; -- Effects --

(def hn-api "https://node-hnapi.herokuapp.com")

(defn fetch
  [url params on-success]
  (-> (js/fetch (str hn-api "/news" "?page=1") (clj->js params))
      (.then js->clj)
      (.then #(js/console.log %))
      (.catch #(js/console.error %))))

(reg-event-fx
 :load-front-page-stories
 (fn [{:keys [db]} [_]]
   {:http-xhrio {:method          :get
                 :uri             (str hn-api "/news")
                 :params {:page  (get-in db [:front-page :current-page-num])}
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:loaded-front-page-stories]
                 :on-failure      [:failed-loading-front-page-stories]}}))

(reg-event-fx
 :load-story-comments
 (fn [{:keys [db]} [_ story-id]]
   {:http-xhrio {:method          :get
                 :uri             (str hn-api "/item/" story-id)
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:loaded-story-comments]
                 :on-failure      [:failed-loading-story-comments]}}))

(reg-event-fx
 :open-story-external
 (fn [cofx [_ story-id]]
   (let [story (story-with-id story-id (get-in cofx [:db :stories]))]
     {:dispatch [:read-story story-id]
      :open-url-external (:url story)})))

(defn- open-url! [url]
  (.openURL rn/linking url))

(reg-fx
 :open-url-external
 (fn [url]
   (open-url! url)))

(defn push-nav-stack
  [db route-name params]
  (-> (update-in db [:navigation :router-state :index] inc)
      (update-in [:navigation :router-state :routes] #(conj % {:route-name :story-detail :params params} ))))

(reg-event-fx
 :nav-story-detail
 (fn [cofx [_ story-id]]
   {:db (push-nav-stack (:db cofx) :story-detail {:story-id story-id})
    :dispatch [:load-story-comments story-id]}))

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

(reg-event-db
 :pop-stack-nav
 validate-spec
 (fn [db _]
   (-> (update-in db [:navigation :router-state :index] dec-to-zero)
        (update-in [:navigation :router-state :routes] pop))))

(reg-event-db
 :push-stack-nav
 validate-spec
 (fn [db [_ route-name params]]
   (push-nav-stack db route-name params)))

(reg-event-fx
 :nav/js
 validate-spec
 (fn [{:keys [db]} [_ type route-name params]]
   (js/console.log "JS NAV" type)
   {:dispatch (case type
                "Navigation/BACK" [:pop-stack-nav]
                "Navigate" [])
    :db       db}))
