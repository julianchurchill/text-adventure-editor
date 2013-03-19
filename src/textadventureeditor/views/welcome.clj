(ns textadventureeditor.views.welcome
  (:require [textadventureeditor.views.common :as common])
  (:use [noir.core :only [defpage defpartial]]
        [noir.fetch.remotes :only [defremote]]
        [hiccup.form :only [text-field text-area label]]
        [hiccup.page :only [html5]]))

(defpartial serialised-properties []
   (label "serialised properties label" "serialised properties")
   (text-area "serialised properties text area" ""))

(defpartial location-properties []
  [:div#location-properties
   (label "location-id-label" "location id")
   (text-field "location id" "no id")
   (label "location-description-label" "location description")
   (text-field "location description" "no description")])

(defpartial exit-properties []
  [:div#exit-properties])

(defpartial item-properties []
  [:div#item-properties])

(defpartial properties []
  [:div#editor
   [:div#properties
    (location-properties)
    (exit-properties)
    (item-properties)]
   [:div#canvas-and-serialisation
    [:canvas#canvas {:width 800 :height 650 :tabindex 1}]
    (serialised-properties)]])

(defpage "/" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]
          (properties)))

(defremote deserialise-locations [text]
  text)