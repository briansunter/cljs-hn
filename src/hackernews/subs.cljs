(ns hackernews.subs
  (:require  [re-frame.core :refer [reg-sub]]
             [hackernews.utils :refer [find-by-id find-first]]))

(reg-sub
 :nav-state
 (fn [db _]
   (get-in db [:navigation :router-state])))
