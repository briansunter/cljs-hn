(ns hackernews.scenes.front-page.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-front-page-stories
 (fn [db _]
   (:stories db)))
