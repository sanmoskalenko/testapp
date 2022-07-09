(ns testapp.ui.events
  (:require
    [re-frame.core :as rf]
    [testapp.ui.db :as db]
    [cljs.reader :as edn]
    [ajax.core :as ajax]
    [day8.re-frame.http-fx]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

(enable-console-print!)

(rf/reg-event-db
  ::initialize-db
  (fn [_ _] db/default-db))

;;;;;;
;; LIST ORDERS
;;;;;;


(rf/reg-event-db
  ::list-orders-success
  (fn [db [_ {:keys [_ data]}]]
    (-> db
        (assoc :loading true)
        (assoc :orders @data))))


(rf/reg-event-db
  ::list-orders-failure
  (fn [db [_ data]]
    (-> db
        (assoc :loading true)
        (assoc :orders data))))


(rf/reg-event-fx
  ::list-orders
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/api/orders"
                  :timeout         8000
                  :response-format {:read         edn/read-string
                                    :description  "testapp cljs project"
                                    :content-type ["application/edn"]}
                  :on-failure      [::list-orders-failure]
                  :on-success      [::list-orders-success]}}))


;;;;;;
;; CREATE ORDER
;;;;;;


(rf/reg-event-db
  ::create-order-success
  (fn [db [_ {:keys [_ data]}]]
    (-> db
        (assoc :loading true)
        (assoc :orders data))))


(rf/reg-event-db
  ::create-order-failure
  (fn [db [_ data]]
    (-> db
        (assoc :orders data))))

(rf/reg-event-fx
  ::create-order
  (fn [{:keys [db]} [_ data]]
    {:http-xhrio {:method          :post
                  :uri             "/api/order"
                  :params          data
                  :timeout         8000
                  :format          {:write        str
                                    :content-type ["application/edn"]}
                  :response-format {:read         edn/read-string
                                    :description  "testapp cljs project"
                                    :content-type ["application/edn"]}
                  :on-success      [::create-order-success data]
                  :on-failure      [::create-order-failure data]}}))


