(ns hackernews.events
  (:require
   [re-frame.core :refer [reg-event-db after reg-event-fx reg-cofx reg-fx]]
   [ajax.core :as ajax]
   [clojure.spec :as s]
   [hackernews.db :as db :refer [app-db]]))

(def ReactNative (js/require "react-native"))
(def linking (.-Linking ReactNative))
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
   (let [stories (get-in db [:front-page :front-page-stories])
         updated-stories (map #(if (= story-id (:id %)) (assoc % :read? true) %) stories)]
     (assoc-in db [:front-page :front-page-stories] updated-stories))))

(reg-event-fx
 :loaded-front-page-stories
 validate-spec
 (fn [cofx [_ stories]]
   {:db (-> (update-in (:db cofx) [:front-page :front-page-stories] #(concat % stories))
            (update-in [:front-page :current-page-num] inc))}))

;; -- Effects --

(def hn-api "https://node-hnapi.herokuapp.com/news")

(reg-event-fx
 :load-front-page-stories
 (fn [{:keys [db]} [_]]
   {:http-xhrio {:method          :get
                 :uri             hn-api
                 :params {:page  (get-in db [:front-page :current-page-num])}
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:loaded-front-page-stories]
                 :on-failure      [:failed-loading-front-page-stories]}}))

(reg-event-fx
 :open-story-external
 (fn [cofx [_ story-id]]
   (let [story (story-with-id story-id (get-in (:db cofx) [:front-page :front-page-stories]))]
     {:dispatch [:read-story story-id]
     :open-url-external (:url story)})))

(defn- open-url! [url]
  (.openURL linking url))

(reg-fx
 :open-url-external
 (fn [url]
   (open-url! url)))
