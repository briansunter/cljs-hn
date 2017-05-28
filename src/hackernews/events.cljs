(ns hackernews.events
  (:require
   [re-frame.core :refer [reg-event-db after reg-event-fx reg-cofx reg-fx dispatch subscribe]]
   [ajax.core :as ajax]
   [clojure.spec :as s]
   [hackernews.navigation :as ios-nav]
   [camel-snake-kebab.core :as kebab]
   [camel-snake-kebab.extras :as kebab-extras]
   [hackernews.api :as api]
   [hackernews.utils :refer [find-by-id dec-to-zero]]
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
  (after (fn [db [e]] (.log js/console "EVENT" (clj->js e)))))

(def interceptors [validate-spec logging])

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :read-story
 validate-spec
 (fn [db [_ story-id]]
   (let [stories (:stories db)
         updated-stories (map #(if (= story-id (:id %)) (assoc % :read? true) %) stories)]
     (assoc db :stories updated-stories))))

(defn format-algolia-response
  [response]
  (->> (map #(kebab-extras/transform-keys kebab/->kebab-case %) response)
       (map #(clojure.set/rename-keys % {:object-id :id :author :user :comment-text :content}))
       (map #(update % :id js/parseInt))))

(reg-event-fx
 :loaded-front-page-stories
 interceptors
 (fn [cofx [_ stories]]
   {:db (-> (update (:db cofx) :stories #(concat % (format-algolia-response (:hits stories))))
            (update-in [:front-page :current-page-num] inc))}))

(reg-event-fx
 :failed-loading-front-page-stories
 interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

(reg-event-fx
 :loaded-story-comments
 interceptors
 (fn [{:keys [db]} [_ id comments]]
   #_(.log js/console "loaded comments" id (clj->js (format-algolia-response (:hits comments))))
   {:db (update db :comments #(concat (format-algolia-response (:hits comments))))}))

(reg-event-fx
 :failed-loading-story-comments
 interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

;; -- Effects --

(def algolia-api "https://hn.algolia.com/api/v1/search")

(reg-fx
 :fetch-http
 (fn [{:as   request
       :keys [url params method on-success on-failure]
       :or   {on-success      [:http-no-on-success]
              on-failure      [:http-no-on-failure]}}]
   (api/fetch (assoc request
                     :on-success #(dispatch (conj on-success %))
                     :on-failure #(dispatch (conj on-failure %))))))

(reg-event-fx
 :load-front-page-stories
 (fn [{:keys [db]} [_]]
   {:fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags "front_page" :page (get-in db [:front-page :current-page-num])}
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:loaded-front-page-stories]
                 :on-failure      [:failed-loading-front-page-stories]}}))

(reg-event-fx
 :load-story-comments
 (fn [{:keys [db]} [_ story-id]]
   {:fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags (str "comment,story_" story-id)}
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:loaded-story-comments story-id]
                 :on-failure      [:failed-loading-story-comments]}}))

(reg-event-fx
 :open-story-external
 (fn [cofx [_ story-id]]
   (let [story (find-by-id story-id (get-in cofx [:db :stories]))]
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
