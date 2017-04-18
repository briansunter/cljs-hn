(ns hackernews.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-front-page-stories
 (fn [db _]
   (get-in db [:front-page :front-page-stories])))
