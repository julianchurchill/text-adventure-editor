(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo])
  (:use [jayq.core :only [$]]))

(def $body ($ :body))

(def editor (canvas/init (.get ($ :#canvas) 0)))

(defn font-style-that-works [ctx color]
  (set! (.-font ctx) color)
  ctx)

(defn fill-style-that-works [ctx color]
  (set! (.-fillStyle ctx) color)
  ctx)

(defn stroke-style-that-works [ctx color]
  (set! (.-strokeStyle ctx) color)
  ctx)

(defn stroke-width-that-works [ctx color]
  (set! (.-lineWidth ctx) color)
  ctx)

(defn alpha-that-works [ctx color]
  (set! (.-globalAlpha ctx) color)
  ctx)

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

