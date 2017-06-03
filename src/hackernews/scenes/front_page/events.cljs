(ns hackernews.scenes.front-page.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch dispatch-sync]]
            [hackernews.utils :refer [index-by-id]]
            [hackernews.interceptors :as i]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extras]))

(def algolia-api "https://hn.algolia.com/api/v1/search")

(reg-event-db
 :read-story
 i/interceptors
 (fn [db [_ story-id]]
   (assoc-in db [:stories story-id :read?] true)))

(defn format-algolia-response
  [response]
  (->> (:hits response)
       (map #(kebab-extras/transform-keys kebab/->kebab-case %))
       (map #(clojure.set/rename-keys % {:object-id :id
                                         :author :user
                                         :comment-text :content}))
       (map #(update % :id js/parseInt))))

(reg-event-fx
 :load-front-page-stories
 i/interceptors
 (fn [{:keys [db]} [_]]
   {:db db
    :fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags "front_page" :page (get-in db [:front-page :current-page-num])}
                 :response-formatter format-algolia-response
                 :timeout         8000
                 :on-success      [:loaded-front-page-stories]
                 :on-failure      [:failed-loading-front-page-stories]}}))

(reg-fx
 :load-comments-for-stories
 (fn [story-ids]
   (.log js/console "EVENT " "comments for story")
   (doseq [id story-ids]
     (dispatch [:load-story-comments id]))))

(reg-event-fx
 :loaded-front-page-stories
 i/interceptors
 (fn [cofx [_ stories]]
   {:db (-> (update (:db cofx) :stories #(merge % (index-by-id stories)))
            (update-in [:front-page :current-page-num] inc))
    ;; :dispatch [:load-story-comments (:id (first stories))]
    :load-comments-for-stories (map :id stories)}))

(reg-event-fx
 :failed-loading-front-page-stories
 i/interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

(reg-event-fx
 :load-story-comments
 i/interceptors
 (fn [{:keys [db]} [_ story-id page]]
   {:db db
    :fetch-http {:method          :get
                 :url             algolia-api
                 :params          {:tags (str "comment,story_" story-id) :page (or page 0)}
                 :timeout         8000
                 :response-formatter format-algolia-response
                 :on-success      [:loaded-story-comments story-id (or page 0)]
                 :on-failure      [:failed-loading-story-comments]}}))

(reg-event-fx
 :loaded-story-comments
 i/interceptors
 (fn [{:keys [db]} [_ id page comments]]
   (merge
    {:db (update db :comments #(merge % (index-by-id comments)))}
    (when-not (empty? comments)
      {:dispatch [:load-story-comments id (inc page)]}))))

(reg-event-fx
 :failed-loading-story-comments
 i/interceptors
 (fn [cofx [_ error-response]]
   (throw (ex-info (str error-response "Failed loading stories") {:response error-response}))
   {:db (:db cofx)}))

(defn push-nav-stack
  [db route-name params]
  (let [current-routes (get-in db [:navigation :router-state :routes])
        last-route (last current-routes)
        next-route {:route-name :story-detail :params params}]
    (if (= (:route-name last-route) (:route-name next-route))
      db
      (-> (update-in db [:navigation :router-state :index] inc)
          (update-in [:navigation :router-state :routes] #(conj % next-route))))))

(reg-event-fx
 :nav-story-detail
 i/interceptors
 (fn [cofx [_ story-id]]
   {:db (push-nav-stack (:db cofx) :story-detail {:story-id story-id})
    :dispatch [:load-story-comments story-id]}))
