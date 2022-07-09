(ns testapp.ui.views
  (:require
    [re-frame.core :as rf]
    [reagent.core :as r]
    [testapp.ui.events :as events]
    [testapp.ui.subs :as subs]))



(enable-console-print!)


(defn list-orders [orders]
  [:div.container
   [:button.btn.btn-sm.btn-primary {:on-click #(rf/dispatch [::events/list-orders])} "show-orders"]
   [:table {:style {:width "100%"}}
    [:tr {:style {:width "100%" :text-align "center"}}
     [:th {:style {:text-align "center"}} "Capture"]
     [:th {:style {:text-align "center"}} "Description"]
     [:th {:style {:text-align "center"}} "Assignee"]
     [:th {:style {:text-align "center"}} "Reporter"]
     [:th {:style {:text-align "center"}} "Closed-date"]]
    [:tbody
     (for [order orders]
       (let [{:keys [order/assignee
                     order/capture
                     order/closed-date
                     order/description
                     order/reporter]} order]
         [:tr
          [:td {:style {:text-align "center"}} capture]
          [:td {:style {:text-align "center"}} description]
          [:td {:style {:text-align "center"}} assignee]
          [:td {:style {:text-align "center"}} reporter]
          [:td {:style {:text-align "center"}} closed-date]]))]]])


(defn create-order []
  (let [capture     (r/atom nil)
        assignee    (r/atom nil)
        description (r/atom nil)
        reporter    (r/atom nil)
        closed-date (r/atom nil)]
    (fn []
      [:div.container
       [:tbody
        [:fieldset.form-group
         [:tr
          [:td [:input.form-control.form-control-lg {:type        "text"
                                                     :placeholder "capture"
                                                     :value       @capture
                                                     :on-change   #(reset! capture (-> % .-target .-value))}]]
          [:td [:input.form-control.form-control-lg {:type        "text"
                                                     :placeholder "description"
                                                     :value       @description
                                                     :on-change   #(reset! description (-> % .-target .-value))}]]
          [:td [:input.form-control.form-control-lg {:type        "text"
                                                     :placeholder "assignee"
                                                     :value       @assignee
                                                     :on-change   #(reset! assignee (-> % .-target .-value))}]]
          [:td [:input.form-control.form-control-lg {:type        "text"
                                                     :placeholder "reporter"
                                                     :value       @reporter
                                                     :on-change   #(reset! reporter (-> % .-target .-value))}]]
          [:td [:input.form-control.form-control-lg {:type        "text"
                                                     :placeholder "closed-date"
                                                     :value       @closed-date
                                                     :on-change   #(reset! closed-date (-> % .-target .-value))}]]]]
        [:button.btn.btn-sm.btn-primary {:on-click #(rf/dispatch [::events/create-order {:assignee    @assignee
                                                                                         :capture     @capture
                                                                                         :closed-date @closed-date
                                                                                         :description @description
                                                                                         :reporter    @reporter}])} "create-order"]]])))


(defn footer []
  [:footer
   [:div.container
    [:span.attribution
     [:a "testapp on Clojure for ARRIVAL"]]]])


(defn header []
  [:header
   [:div.container
    [:span.code-listing--highlighted
     [:h1 "(testapp)"]]]])


(defn main-panel []
  (let [orders (rf/subscribe [::subs/orders])]
    [:div.home-page
     [header]
     [:div.container
      [create-order]
      [list-orders @orders]]
     [footer]]))



