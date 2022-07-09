(ns testapp.server.api-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [testapp.server.api :as sut]
    [testapp.server.db.datomic :as db]
    [unifier.response :as r]))


(deftest ^:unit add-order-test
  (testing "Request with data to create an order is processed correctly"
    (with-redefs [db/add-order (constantly
                                 (r/as-success
                                   {:db/id             17592186045422,
                                    :order/id          #uuid"7f1d3725-02f9-4f0a-b045-a1b222251e8a"
                                    :order/capture     "CAPTURE"
                                    :order/description "DESCRIPTION"
                                    :order/reporter    "REPORTER"
                                    :order/assignee    "ASSIGNEE"
                                    :order/closed-date "Thu Jul 07 17:40:08 MSK 2022"}))]
      (let [exp {:status  200,
                 :headers {},
                 :body    {:db/id             17592186045422,
                           :order/id          #uuid"7f1d3725-02f9-4f0a-b045-a1b222251e8a",
                           :order/capture     "CAPTURE",
                           :order/description "DESCRIPTION",
                           :order/reporter    "REPORTER",
                           :order/assignee    "ASSIGNEE",
                           :order/closed-date "Thu Jul 07 17:40:08 MSK 2022"}}
            res (sut/add-order {:capture     "CAPTURE"
                                :description "DESCRIPTION"
                                :reporter    "REPORTER"
                                :assignee    "ASSIGNEE"
                                :closed-date "Thu Jul 07 17:40:08 MSK 2022"})]
        (is (= res exp)))))


  (testing "Exceptions when working with the database are handled correctly"
    (with-redefs [db/find-by-id (constantly #(throw (ex-info "test exception" {})))]
      (let [res (sut/add-order {:body-params {:capture     "CAPTURE"
                                              :description "DESCRIPTION"
                                              :reporter    "REPORTER"
                                              :assignee    "ASSIGNEE"
                                              :closed-date "Thu Jul 07 15:43:12 MSK 2022"}})
            exp {:status  400
                 :headers {}
                 :body    {:error/msg    "Error creating record in database"
                           :error/params {:order/assignee    "ASSIGNEE"
                                          :order/capture     "CAPTURE"
                                          :order/closed-date "Thu Jul 07 15:43:12 MSK 2022"
                                          :order/description "DESCRIPTION"
                                          :order/reporter    "REPORTER"}}}]
        (is (= exp res)))))


  (testing "A record with an empty field is not created"
    (let [res (sut/add-order {:capture     "CAPTURE"
                              :description "DESCRIPTION"
                              :reporter    "REPORTER"
                              :assignee    nil
                              :closed-date "Thu Jul 07 15:43:12 MSK 2022"})
          exp {:status 400, :headers {}, :body #:error{:msg "All attributes must be non-null"}}]
      (is (= res exp)))))
