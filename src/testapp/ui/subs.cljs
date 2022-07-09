(ns testapp.ui.subs
  (:require
    [re-frame.core :as rf]
    [clojure.edn :as edn]))


(rf/reg-sub
  ::orders
  (fn [db _]
    (edn/read-string (get-in db [:orders :original-text]))))
