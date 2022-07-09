(ns testapp.server.core
  (:require
    [mount.core :as mount]
    [testapp.server.config :refer [env]]
    [testapp.server.api :refer [webserver]]
    [testapp.server.db.datomic :refer [conn show-schema install-schema]]
    [cambium.core :as log])
  (:gen-class))


(defn stop []
  (log/info {:msg "Stop app"})
  (let [status (mount/stop)]
    (log/info {:status status})
    status))


(defn start []
  (log/info {:msg "Start testapp"})
  (let [status (mount/start)
        _      (.addShutdownHook (Runtime/getRuntime) (Thread. stop))]
    (log/info {:status status})
    status))


(defn restart []
  (stop)
  (start)
  (install-schema conn)
  (show-schema conn))

(defn -main []
  (start)
  (install-schema conn)
  (log/info {:msg       "testapp started"
             :db-schema (show-schema conn)}))

(comment

  (restart)

  )
