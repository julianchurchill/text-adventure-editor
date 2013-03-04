(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo]
            [goog.dom :as dom])
  (:use-macros [cljs.core :only [this-as]])
  (:use [jayq.core :only [$ bind]]
        [textadventureeditor.client.monetfixes 
         :only [font-style-that-works
                fill-style-that-works
                stroke-style-that-works
                stroke-width-that-works
                alpha-that-works]]))

(def $body ($ :body))

(def editor (canvas/init (.get ($ :#canvas) 0)))

(defn draw-editor [ctx me]
  (-> ctx
      (fill-style-that-works "143")
      (stroke-style-that-works "#175")
      (stroke-width-that-works 2)
      (canvas/rect me)
      (canvas/stroke)))

(canvas/add-entity editor :editor
                   (canvas/entity {:x 0 :y 0 :w 800 :h 650}
                                  nil ;;update function
                                  draw-editor))

(def locations (atom {}))

(defn make-location [x y id description]
  (if-not (@locations [x y])
    (swap! locations assoc [x y]
           {:x x :y y :w 40 :h 40 :type :location :id id :description description})
    (swap! locations dissoc [x y])))

(defn draw-location [ctx location]
  (-> ctx
      (fill-style-that-works "222")
      (stroke-style-that-works "#175")
      (stroke-width-that-works 2)
      (canvas/rect location)
      (canvas/stroke)))

(defn draw-locations [ctx me]
  (doseq [l (vals @locations)]
    (draw-location ctx l)))

(canvas/add-entity editor :locations
                   (canvas/entity {}
                                  nil ;; update function
                                  draw-locations))

(make-location 100 100 "loc1" "description1")
(make-location 300 200 "loc2" "description2")
(make-location 300 300 "loc3" "description3")

(defn set-value [id val]
  (set! (.-value (dom/getElement id)) val))

(set-value "location id" "new id")
(set-value "location description" "new description")

(bind ($ :#canvas) :focus
      (fn [e]
        (this-as me
          (set! (.-focused me) true))))

(bind ($ :#canvas) :blur
      (fn [e]
        (this-as me
          (set! (.-focused me) false))))

(bind ($ :#canvas) :mousedown
      (fn [e]
        (this-as me
          (when (.-focused me)
            (make-location (.-offsetX e) (.-offsetY e) "new loc id" "new loc description")))))
