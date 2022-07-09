(ns testapp.server.api
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.util.response :refer [redirect response]]
    [ring.util.http-response :as response]
    [testapp.server.config :refer [env]]
    [mount.core :refer [defstate]]
    [muuntaja.middleware :as middleware]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [testapp.server.db.datomic :as db :refer [conn]]
    [cambium.core :as log]
    [unifier.response :as r]
    [selmer.parser :as parser]))

(parser/set-resource-path!  (clojure.java.io/resource "public"))

(defn home-page []
  (parser/render-file "index.html" {}))


(defn error-page [request]
  (let [error-details {:status (:status request)
                       :header {"Content-Type" "text/html; charset=utf-8"}}]
    (parser/render-file "error.html" error-details)))


(defn add-order
  "Create order request handler"
  [request]
  (log/info {:msg    "Receive request to add new order"
             :params (:body-params request)})
  (let [params (:body-params request)
        order  (db/add-order params)]
    (if (r/success? order)
      (-> order r/get-data response/ok)
      (-> order r/get-data response/bad-request))))


(defn list-orders []
  "Request handler for getting a list of orders"
  (log/info {:msg "Receive request to list orders"})
  (let [orders (db/list-orders)]
    (if (r/success? orders)
      (-> orders r/get-data response/ok)
      (-> orders r/get-data response/not-found))))


(defroutes app-routes
  (ANY "/" [] (redirect "/testapp"))
  (GET "/testapp" [] (home-page))
  (context "/api" []
   (GET "/orders" [] (list-orders))
   (POST "/order" request (add-order request))
   (PUT "/order" [] (error-page (response/not-implemented)))
   (DELETE "/order" [] (error-page (response/not-implemented))))
  (route/not-found (error-page (response/not-found))))


(def app
  (-> app-routes
      middleware/wrap-format
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post])))


(defstate webserver
  :start (do
           (log/info {:msg "Start webserver!"})
           (run-jetty #'app (:webserver env)))
  :stop (do
          (log/info {:msg "Stop webserver"})
          (.stop webserver)))

(comment

  (app-routes {:uri            "/api/orders"
               :request-method :get})

  (app-routes {:uri            "/api/order"
               :request-method :post
               :body-params    {:assignee    "SOME-IVAN",
                                :capture     "SOME-CAPTURE",
                                :closed-date "2022-07-02T23:34:16.0",
                                :description "SOME-DESCRIPTION",
                                :reporter    "SOME-REPORTER"}})

  (app-routes {:uri            "/"
               :request-method :get})

  )