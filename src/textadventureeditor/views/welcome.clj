(ns textadventureeditor.views.welcome
  (:require [textadventureeditor.views.common :as common])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.form :only [form-to text-field]]
        [hiccup.page :only [html5]]))

(defpartial properties [loc]
  [:div#properties
   (form-to [:post loc]
    (text-field "location id" "no id")
    (text-field "location description" "no description"))])

(defpartial editor [loc]
  [:div#editor
   (properties loc)
   [:canvas#canvas {:width 800 :height 650 :tabindex 1}]])

(defpage "/" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]
          (editor "/submit")))
