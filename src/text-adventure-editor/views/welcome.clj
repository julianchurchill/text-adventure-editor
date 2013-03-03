(ns text-adventure-editor.views.welcome
  (:require [text-adventure-editor.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to text-adventure-editor"]))
