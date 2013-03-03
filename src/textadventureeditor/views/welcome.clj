(ns textadventureeditor.views.welcome
  (:require [textadventureeditor.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.page :only [html5]]))

(defpage "/" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]))
