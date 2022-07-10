(ns testapp.server.db.datomic
  (:require
    [datomic.api :as d]
    [cambium.core :as log]
    [testapp.server.config :refer [env]]
    [io.rkn.conformity :as c]
    [mount.core :refer [defstate]]
    [clojure.string :as string]
    [unifier.response :as r])
  (:import
    (java.util
      UUID)))

(defstate conn
  :start (do
           (log/info {:msg "Connect to Datomic database"})
           (-> env :database-url d/create-database)
           (-> env :database-url d/connect))
  :stop (do
          (log/info {:msg "Disconnect Datomic database"})
          (-> conn .release)))


(defn install-schema
  "Expected to be called at system start up."
  [conn]
  (log/info {:msg "Install Datomic schema"})
  (let [norms-map (c/read-resource "schema.edn")]
    (c/ensure-conforms conn norms-map (keys norms-map))))


(defn show-schema
  "Show currently installed schema"
  [conn]
  (let [system-ns #{"db" "db.type" "db.install" "db.part"
                    "db.lang" "fressian" "db.unique" "db.excise"
                    "db.cardinality" "db.fn" "db.sys" "db.bootstrap"
                    "db.alter"}]
    (d/q '[:find ?ident
           :in $ ?system-ns
           :where
           [?e :db/ident ?ident]
           [(namespace ?ident) ?ns]
           [((comp not contains?) ?system-ns ?ns)]]
         (d/db conn) system-ns)))


(defn list-orders []
  "Returns order list"
  (let [orders (mapv #(d/pull (d/db conn) '[*] %)
                     (d/q '[:find [?e ...] :where [?e :order/id]] (d/db conn)))]
    (if-not (nil? orders)
      (r/as-success orders)
      (r/as-not-found {:error/msg "Orders not found"}))))


(defn find-by-id
  "Returns an order by `order-id`"
  [order-id]
  (let [db     (d/db conn)
        db-id  (ffirst (d/q '[:find ?e :in $ ?order-id :where [?e :order/id ?order-id]] db order-id))
        entity (d/pull db '[*] db-id)]
    (if-not (nil? entity)
      (r/as-created entity)
      (r/as-not-found {:error/data order-id
                       :error/msg  (format "Order with id %s not found" order-id)}))))


(defn add-order
  "Creates an order record in the database"
  [{:keys [capture description reporter assignee closed-date]}]
  (try
    (let [order-id (UUID/randomUUID)
          temp-id  (d/tempid :db.part/user)]
      (if (true? (first (filterv true? (map string/blank? [capture description reporter assignee closed-date]))))
        (r/as-incorrect {:error/msg "All attributes must be non-null"})
        (do @(d/transact conn [{:db/id temp-id :order/id order-id}
                               {:db/id temp-id :order/capture capture}
                               {:db/id temp-id :order/description description}
                               {:db/id temp-id :order/reporter reporter}
                               {:db/id temp-id :order/assignee assignee}
                               {:db/id temp-id :order/closed-date closed-date}])
            (find-by-id order-id))))
    (catch Exception e
      (log/error {:msg    "Error creating record in database"
                  :params {:order/capture     capture
                           :order/description description
                           :order/reporter    reporter
                           :order/assignee    assignee
                           :order/closed-date closed-date}})
      (r/as-error {:error/params {:order/capture     capture
                                  :order/description description
                                  :order/reporter    reporter
                                  :order/assignee    assignee
                                  :order/closed-date closed-date}
                   :error/msg    "Error creating record in database"}
                  {:error/description (ex-message e)}))))


(comment

  (list-orders)

  (install-schema conn)

  (show-schema conn)

  (seq (d/tx-range (d/log conn) nil nil))

  (add-order {:capture     "CAPTURE"
              :description "DESCRIPTION"
              :reporter    "REPORTER"
              :assignee    "ASSIGNEE"
              :closed-date (str (java.util.Date.))})

  (d/q '[:find ?ident :where [:db.part/db :db.install/partition ?p]
         [?p :db/ident ?ident]]
       (d/db conn))

  )
