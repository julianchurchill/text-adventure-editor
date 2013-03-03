(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo])
  (:use [jayq.core :only [$]]))

(def $body ($ :body))

(def canvas (canvas/init (.get ($ :#canvas) 0)))

(canvas/add-entity canvas :background
                   (canvas/entity {:x 0 :y 0 :w 600 :h 650}
                                  nil ;;update function
                                  (fn [ctx me]
                                    (-> ctx
                                        (canvas/fill-style "#666")
                                        (canvas/rect me)))))

(js/alert "Hey there!")
