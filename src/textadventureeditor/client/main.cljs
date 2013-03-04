(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo])
  (:use [jayq.core :only [$]]
        [textadventureeditor.client.monetfixes 
         :only [font-style-that-works
                fill-style-that-works
                stroke-style-that-works
                stroke-width-that-works
                alpha-that-works]]))

(def $body ($ :body))

(def editor (canvas/init (.get ($ :#canvas) 0)))

(defn draw-box [ctx me]
  (-> ctx
      (fill-style-that-works "143")
      (stroke-style-that-works "#175")
      (stroke-width-that-works 2)
      (canvas/rect me)
      (canvas/stroke)))

(canvas/add-entity editor :editor
                   (canvas/entity {:x 0 :y 0 :w 800 :h 650}
                                  nil ;;update function
                                  draw-box))

