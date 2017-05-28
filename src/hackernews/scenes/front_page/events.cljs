(ns hackernews.scenes.front-page.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx]]
            [hackernews.interceptors :as i]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extras]))

(def algolia-api "https://hn.algolia.com/api/v1/search")

(reg-event-db
 :read-story
 i/interceptors
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
 :load-front-page-stories
 i/interceptors
 (fn [{:keys [db]} [_]]
   {:db db
    :fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags "front_page" :page (get-in db [:front-page :current-page-num])}
                 :timeout         8000
                 :on-success      [:loaded-front-page-stories]
                 :on-failure      [:failed-loading-front-page-stories]}}))

(reg-event-fx
 :loaded-front-page-stories
 i/interceptors
 (fn [cofx [_ stories]]
   {:db (-> (update (:db cofx) :stories #(concat % (format-algolia-response (:hits stories))))
            (update-in [:front-page :current-page-num] inc))}))

(reg-event-fx
 :failed-loading-front-page-stories
 i/interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

(reg-event-fx
 :load-story-comments
 i/interceptors
 (fn [{:keys [db]} [_ story-id]]
   {:db db
    :fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags (str "comment,story_" story-id)}
                 :timeout         8000
                 :on-success      [:loaded-story-comments story-id]
                 :on-failure      [:failed-loading-story-comments]}}))

(reg-event-fx
 :loaded-story-comments
 i/interceptors
 (fn [{:keys [db]} [_ id comments]]
   #_(.log js/console "loaded comments" id (clj->js (format-algolia-response (:hits comments))))
   {:db (update db :comments #(concat (format-algolia-response (:hits comments))))}))

(reg-event-fx
 :failed-loading-story-comments
 i/interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

(defn push-nav-stack
  [db route-name params]
  (-> (update-in db [:navigation :router-state :index] inc)
      (update-in [:navigation :router-state :routes] #(conj % {:route-name :story-detail :params params}))))

(reg-event-fx
 :nav-story-detail
 i/interceptors
 (fn [cofx [_ story-id]]
   {:db (push-nav-stack (:db cofx) :story-detail {:story-id story-id})
    :dispatch [:load-story-comments story-id]}))
