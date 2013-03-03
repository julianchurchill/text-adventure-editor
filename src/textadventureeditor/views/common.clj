(ns textadventureeditor.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "text-adventure-editor"]
               (include-css "/css/reset.css")
               (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js")]
              [:body
               [:div#wrapper
                content]]
             (include-js "/js/main.js")))
