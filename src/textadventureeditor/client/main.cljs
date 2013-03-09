(ns textadventureeditor.client.main
  (:require [monet.canvas :as canvas]
            [monet.geometry :as geo]
            [goog.dom :as dom]
            [crate.core :as crate])
  (:use-macros [cljs.core :only [this-as]]
               [crate.def-macros :only [defpartial]])
  (:use [jayq.core :only [$ bind append remove delegate data]]
        [textadventureeditor.client.monetfixes 
         :only [font-style-that-works
                fill-style-that-works
                stroke-style-that-works
                stroke-width-that-works
                alpha-that-works]]
        [crate.form :only [text-field]]))

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

(defn make-location [values]
  (let [x (:x values)
        y (:y values)]
    (if-not (@locations [x y])
      (swap! locations assoc [x y]
             (conj values {:w 40 :h 40 :type :location :current false})))
    (@locations [x y])))
  
(defn loc-fill-style [location]
  (if (:current location)
    "87"
	  "222"))

(defn loc-stroke-style [location]
  (if (:current location)
    "154"
	  "175"))

(defn loc-stroke-width [location]
  (if (:current location)
    5
  	1))

(defn font-size [ctx size]
  (set! (.-fontSize ctx) size)
  ctx)

(defn draw-location [ctx location]
  (-> ctx
      (fill-style-that-works (loc-fill-style location))
      (stroke-style-that-works (loc-stroke-style location))
      (stroke-width-that-works (loc-stroke-width location))
      (canvas/rect location)
      (canvas/stroke))
  (-> ctx
      (fill-style-that-works "222")
      (font-size "large")
      (canvas/text {:x (:x location) :y (:y location) :text (:id location)})))

(defn draw-locations [ctx me]
  (doseq [l (vals @locations)]
    (draw-location ctx l)))

(canvas/add-entity editor :locations
                   (canvas/entity {}
                                  nil ;; update function
                                  draw-locations))

(defn set-value [id val]
  (set! (.-value (dom/getElement id)) val))

(defn get-value [id]
  (.-value (dom/getElement id)))

(bind ($ :#canvas) :focus
      (fn [e]
        (this-as me
          (set! (.-focused me) true))))

(bind ($ :#canvas) :blur
      (fn [e]
        (this-as me
          (set! (.-focused me) false))))

;;;;;;;;;;;
;; Exits ;;
;;;;;;;;;;;

(def $exit-properties ($ :#exit-properties))

(def exit-id-field-id "exit-id")
(def exit-label-field-id "exit-label")
(def exit-destination-field-id "exit-destination")
(def exit-direction-hint-field-id "exit-direction-hint")
(def exit-delete-id "delete-exit")
(def exit-div-id "single-exit")

(def next-available-exit-index (atom 0))
(def exit-indices-for-current-location (atom []))

(defpartial exit-props-field [{:keys [name value]}]
  (text-field name value))

(defpartial delete-exit-props-button [{:keys [label action param id]}]
  [:a.button.delete-exit-button {:href "#" :data-action action :data-param param :id id} label])

(defpartial exit-div [{:keys [id]}]
  [:div {:id id}])

(defn $exit-div [index]
  ($ (str "#" exit-div-id index)))

(defn add-fields-for-exit [{:keys [id label destination direction-hint]}]
  (swap! next-available-exit-index inc)
  (swap! exit-indices-for-current-location conj @next-available-exit-index)
  (append $exit-properties 
          (exit-div {:id (str exit-div-id @next-available-exit-index)}))
  (append ($exit-div @next-available-exit-index)
          (exit-props-field {:name (str exit-id-field-id @next-available-exit-index)
                             :value id}))
  (append ($exit-div @next-available-exit-index)
          (exit-props-field {:name (str exit-label-field-id @next-available-exit-index)
                             :value label}))
  (append ($exit-div @next-available-exit-index)
          (exit-props-field {:name (str exit-destination-field-id @next-available-exit-index)
                             :value destination}))
  (append ($exit-div @next-available-exit-index)
          (exit-props-field {:name (str exit-direction-hint-field-id @next-available-exit-index)
                             :value direction-hint}))
  (append ($exit-div @next-available-exit-index)
          (delete-exit-props-button {:label "delete"
                                     :action (str exit-delete-id @next-available-exit-index)
                                     :param @next-available-exit-index
                                     :id (str exit-delete-id @next-available-exit-index)})))

(defn discard-value [values value]
  (filter #(not (= value %)) values))

(defn remove-fields-for-exit [index]
  (remove ($exit-div index)))

(defn handle-delete-exit [index]
  (remove-fields-for-exit index)
  (swap! exit-indices-for-current-location discard-value index))

(delegate $body delete-exit-props-button :click
          (fn [e]
            (.preventDefault e)
            (this-as me 
                     (let [$me ($ me)
                           index (data $me :param)]
                       (handle-delete-exit index)))))
                    
(defn show-location-exits [location]
	(doall (map remove-fields-for-exit @exit-indices-for-current-location))
  (swap! exit-indices-for-current-location (fn [n] []))  
  (swap! next-available-exit-index (fn [n] 0))
  (doall (map #(add-fields-for-exit %) (:exits location))))

(defn make-exit-from-fields [index]
  {:id (get-value (str exit-id-field-id index))
   :label (get-value (str exit-label-field-id index))
   :destination (get-value (str exit-destination-field-id index))
   :direction-hint (get-value (str exit-direction-hint-field-id index))})

(defn gather-exits-values []
  ; for each available exit, extract all the values from each field
  (doall (map make-exit-from-fields @exit-indices-for-current-location)))

;;;;;;;;;;;;;;;
;; Locations ;;
;;;;;;;;;;;;;;;

(def loc-id-field-id "location id")
(def loc-description-field-id "location description")

(defn find-current-location []
  (first (filter #(:current %) (vals @locations))))

(defn change-location-property [location param value]
  (swap! locations assoc [(:x location) (:y location)] 
         (assoc location param value)))

(defn show-location-information [location]
  (set-value loc-id-field-id (:id location))
  (set-value loc-description-field-id (:description location)))

(defn make-location-current [location]
  (let [currentloc (find-current-location)]
    (when currentloc
		  (change-location-property currentloc :current false)))
  (change-location-property location :current true)
  (show-location-information location)
  (show-location-exits location))

(defn make-new-location [x y]
  (make-location-current 
   (make-location {:x x :y y :id "new id" :description "new description" :exits [] :items []})))

(defn location-at [x y]
  (first (filter #(geo/in-bounds? % x y) (vals @locations))))

(defn canvas-mousedown [e]
  (this-as me
           (let [x (.-offsetX e)
                 y (.-offsetY e)]
             (let [location (location-at x y)]
               (if location
                 (make-location-current location)
                 (make-new-location x y))))))

(bind ($ :#canvas) :mousedown
      canvas-mousedown)

(make-location {:x 100 :y 100 :id "loc1" :description "description1"
                :exits [{:id "exit1" :label "north" :destination "loc1" :direction-hint "NORTH"}
                        {:id "exit2" :label "east" :destination "loc2" :direction-hint "EAST"}]
                :items [{:id "" :name "" :description "" :countable-noun-prefix "a" :mid-sentence-cased-name ""
                         :is-untakeable false :can-be-used-with "" :successful-use-message ""
                         :use-is-not-repeatable false :use-actions []}]})
(make-location {:x 300 :y 200 :id "loc2" :description "description2"
                :exits [{:id "exit1" :label "south" :destination "loc1" :direction-hint "SOUTH"}]
                :items []})
(make-location {:x 300 :y 300 :id "loc3" :description "description3" :exits [] :items []})

(make-location-current (first (vals @locations)))

(def $location-props ($ :#location-properties))

(defpartial locprops-add-exit-button [{:keys [label action param]}]
  [:a.button.add-exit-button {:href "#" :data-action action :data-param param} label])

(append $location-props (locprops-add-exit-button {:label "add exit"
                                                   :action "add-exit"
                                                   :param ""}))

(defn default-exit []
  {:id "default id"
   :label "default label"
   :destination "default destination"
   :direction-hint "default direction hint"})

(defn handle-locprops-add-exit [event]
  (.preventDefault event)
  (add-fields-for-exit (default-exit)))

(delegate $body locprops-add-exit-button :click
          handle-locprops-add-exit)

(defpartial locprops-save-button [{:keys [label action param]}]
  [:a.button.save-button {:href "#" :data-action action :data-param param} label])

(append $location-props (locprops-save-button {:label "save"
                                               :action "save-location"
                                               :param ""}))

(defn handle-locprops-save [event]
  (.preventDefault event)
  (change-location-property (find-current-location) :id (get-value loc-id-field-id))
  (change-location-property (find-current-location) :description (get-value loc-description-field-id))
  (change-location-property (find-current-location) :exits (gather-exits-values)))

(delegate $body locprops-save-button :click
          handle-locprops-save)