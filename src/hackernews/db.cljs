(ns hackernews.db
  (:require [clojure.spec :as s]))

;; spec of app-db
(s/def ::greeting string?)

(s/def ::id pos-int?)
(s/def ::title string?)
(s/def ::points pos-int?)
(s/def ::user string?)
(s/def ::time pos-int?)
(s/def ::time-ago string?)
(s/def ::comments-count string?)
(s/def ::type string?)
(s/def ::url string?)
(s/def ::domain string?)
(s/def ::story (s/keys :req-un [::id ::title]))
(s/def ::stories (s/coll-of ::story))
(s/def ::front-page-stories ::stories)
(s/def ::current-page-num int?)
(s/def ::front-page (s/keys :req-un [::front-page-stories] :opt-un [::current-page-num]))
(s/def ::app-db
  (s/keys :req-un [::greeting ::front-page]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :front-page {:front-page-stories [{:id 1 :title "Foo"} {:id 2 :title "Bar"} {:id 3 :title "Bas"}]}})
