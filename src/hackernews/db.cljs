(ns hackernews.db
  (:require [clojure.spec :as s]))

;; spec of app-db
(s/def ::greeting string?)

(s/def ::id pos-int?)
(s/def ::title string?)
(s/def ::points (s/nilable int?))
(s/def ::user string?)
(s/def ::time pos-int?)
(s/def ::time-ago string?)
(s/def ::comments-count string?)
(s/def ::type string?)
(s/def ::url (s/nilable string?))
(s/def ::domain string?)
(s/def ::content string?)
(s/def ::comment (s/keys :req-un [::content]))
(s/def ::comments (s/coll-of ::comment))
(s/def ::story (s/keys :req-un [::id ::title ::points] :opt-un [::read? ::comments]))
(s/def ::distinct-ids #(distinct? (map :id %)))
(s/def ::stories (s/and (s/coll-of ::story) ::distinct-ids))

(s/def ::current-page-num int?)
(s/def ::front-page (s/keys :req-un [::current-page-num]))
(s/def ::app-db (s/keys :req-un [::stories ::front-page]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :stories []
             :front-page {:current-page-num 1}})
