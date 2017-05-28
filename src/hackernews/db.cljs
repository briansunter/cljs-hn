(ns hackernews.db
  (:require [clojure.spec :as s]))

;; spec of app-db
(s/def ::greeting string?)

(s/def ::id pos-int?)
(s/def ::title string?)
(s/def ::points (s/nilable int?))
(s/def ::author string?)
(s/def ::time pos-int?)
(s/def ::time-ago string?)
(s/def ::created-at string?)
(s/def ::num-comments string?)
(s/def ::type string?)
(s/def ::url (s/nilable string?))
(s/def ::domain string?)
(s/def ::content string?)
(s/def ::comment (s/keys :req-un [::id ::content ::story-id]))
(s/def ::comments (s/coll-of ::comment))
(s/def ::story (s/keys :req-un [::id ::title ::points] :opt-un [::read? ::comments]))
(s/def ::distinct-ids #(distinct? (map :id %)))
(s/def ::stories (s/and (s/coll-of ::story) ::distinct-ids))

(s/def ::current-page-num int?)
(s/def ::front-page (s/keys :req-un [::current-page-num]))

(s/def ::route-name keyword?)
(s/def ::params map?)
(s/def ::route (s/keys :req-un [::route-name] :opt-un [::params]))
(s/def ::routes (s/coll-of ::route))
(s/def ::index int?)

(s/def ::router-state (s/keys :req-un [::routes ::index]))
(s/def ::navigation (s/keys :req-un [::router-state]))
(s/def ::app-db (s/keys :req-un [::stories ::front-page ::navigation]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :navigation {:router-state {:routes [{:route-name :front-page :key "front-page"}]
                                         :index 0}}
             :stories []
             :front-page {:current-page-num 0}})
