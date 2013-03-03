(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo])
  (:use [jayq.core :only [$]]))

(def $body ($ :body))

(def editor (canvas/init (.get ($ :#canvas) 0)))

(defn fill-style-that-works [ctx color]
  (set! (.-fillStyle ctx) color)
  ctx)

(defn fill-style-that-doesnt-work [ctx color]
  (set! ctx.fillStyle color)
  ctx)

(defn draw-box [ctx me]
  (-> ctx
;      (canvas/fill-style "#143")
      (fill-style-that-works "143")
;      (fill-style-that-doesnt-work "143")
      (canvas/stroke-style "#175")
      (canvas/stroke-width 2)
      (canvas/rect me)
      (canvas/stroke)))

(canvas/add-entity editor :editor
                   (canvas/entity {:x 0 :y 0 :w 800 :h 650}
                                  nil ;;update function
                                  draw-box))

