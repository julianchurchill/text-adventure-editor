(ns textadventureeditor.views.welcome
  (:require [textadventureeditor.views.common :as common])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page :only [html5]]))

(defpartial editor []
  [:div#editor
   [:canvas#canvas {:width 800 :height 650 :tabindex 1}]])

(defpage "/" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]
          (editor)))
