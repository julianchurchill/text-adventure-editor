(ns textadventureeditor.views.welcome
  (:require [textadventureeditor.views.common :as common])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.form :only [form-to text-field submit-button]]
        [hiccup.page :only [html5]]))

(defpartial location-properties []
  (form-to [:post "/locprops"]
           [:div#location-properties
            (text-field "location id" "no id")
            (text-field "location description" "no description")]))

(defpartial properties []
  [:div#editor
   [:div#properties
    (location-properties)
    [:canvas#canvas {:width 800 :height 650 :tabindex 1}]]])

(defpage "/" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]
          (properties)))
