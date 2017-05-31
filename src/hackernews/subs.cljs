(ns hackernews.subs
  (:require  [re-frame.core :refer [reg-sub]]))

(reg-sub
 :nav-state
 (fn [db _]
   (get-in db [:navigation :router-state])))
