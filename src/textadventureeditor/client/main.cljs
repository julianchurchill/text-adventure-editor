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
        [crate.form :only [text-field label]]))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Sub property (e.g. items, exits) handling functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial make-text-field [{:keys [name value]}]
  (text-field name value))

(defpartial make-div [{:keys [id]}]
  [:div {:id id}])

(defpartial make-label [{:keys [name value]}]
  (label name value))

; Add a label and a text field to a div
(defn extract-text-field-and-label [field-info value div next-available-index]
  (append div (make-label {:name (str (:field-id field-info) "-label" next-available-index)
                           :value (:label field-info)}))
  (append div (make-text-field {:name (str (:field-id field-info) next-available-index)
                                :value value})))

(defn discard-value [values value]
  (filter #(not (= value %)) values))

(defn make-map-from-fields [index fields-info]
  (reduce into (doall (map #(assoc {} % (get-value (str (:field-id (% fields-info)) index)))
                (keys fields-info)))))

(defn show-location-sub-properties [location property]
  (let [div-func (:div-func property)
        indices-atom (:indices-atom property)
        next-index-atom (:next-index-atom property)
        field-adding-func (:field-adding-func property)
        location-property (:location-property property)]
    (doall (map #(remove (div-func %)) @indices-atom))  ; remove all divs for sub-property
    (swap! indices-atom (fn [n] []))                    ; reset indices
    (swap! next-index-atom (fn [n] 0))                  ; reset next index
    (doall (map #(field-adding-func %) (location-property location))))) ; convert location property into populated fields

(defn gather-values-for-sub-property [property]
  (doall (map #(make-map-from-fields % (:fields-info property)) @(:indices-atom property))))

(defn add-delete-handler-for-location-sub-property [property]
  (delegate $body (:delete-button-partial-func property) :click
            (fn [e]
              (.preventDefault e)
              (this-as me 
                       (let [$me ($ me)
                             index (data $me :param)]
                         (remove ((:div-func property) index))
                         (swap! (:indices-atom property) discard-value index))))))

;;;;;;;;;;;
;; Exits ;;
;;;;;;;;;;;

(def $exit-properties ($ :#exit-properties))

(def exit-fields-info
  {:id {:field-id "exit-id" :label "exit id"}
   :label {:field-id "exit-label" :label "exit label"}
   :destination {:field-id "exit-destination" :label "exit destination"}
   :direction-hint {:field-id "exit-direction-hint" :label "exit direction hint"}})

(def exit-delete-id "delete-exit")
(def exit-div-id "single-exit")

(def next-available-exit-index (atom 0))
(def exit-indices-for-current-location (atom []))

(defpartial delete-exit-props-button [{:keys [label action param id]}]
  [:a.button.delete-exit-button {:href "#" :data-action action :data-param param :id id} label])

(defn $exit-div [index]
  ($ (str "#" exit-div-id index)))

(defn add-fields-for-exit [values]
  (swap! next-available-exit-index inc)
  (swap! exit-indices-for-current-location conj @next-available-exit-index)
  (append $exit-properties 
          (make-div {:id (str exit-div-id @next-available-exit-index)}))
  (doall (map #(extract-text-field-and-label (% exit-fields-info)
                                             (% values)
                                             ($exit-div @next-available-exit-index)
                                             @next-available-exit-index)
              (keys values)))
  (append ($exit-div @next-available-exit-index)
          (delete-exit-props-button {:label "delete"
                                     :action (str exit-delete-id @next-available-exit-index)
                                     :param @next-available-exit-index
                                     :id (str exit-delete-id @next-available-exit-index)})))

(def exits-sub-property
    {:div-func $exit-div
     :indices-atom exit-indices-for-current-location
     :next-index-atom next-available-exit-index
     :field-adding-func add-fields-for-exit
     :location-property :exits
     :fields-info exit-fields-info
     :delete-button-partial-func delete-exit-props-button})

(add-delete-handler-for-location-sub-property exits-sub-property)

;;;;;;;;;;;
;; Items ;;
;;;;;;;;;;;

(def $item-properties ($ :#item-properties))

(def item-fields-info
  {:id {:field-id "item-id" :label "item id"}
   :name {:field-id "item-name" :label "item name"}
   :description {:field-id "item-description" :label "item description"}
   :countable-noun-prefix {:field-id "item-countable-noun-prefix" :label "item countable noun prefix"}
   :mid-sentence-cased-name {:field-id "item-mid-sentence-cased-name" :label "item mid sentence cased name"}
   :is-untakeable {:field-id "item-is-untakeable" :label "item is untakeable"}
   :can-be-used-with {:field-id "item-can-be-used-with" :label "item can be used with"}
   :successful-use-message {:field-id "item-successful-use-message" :label "item successful use message"}
   :use-is-not-repeatable {:field-id "item-use-is-not-repeatable" :label "item use is not repeatable"}
   :use-actions {:field-id "item-use-actions" :label "item use actions"}})

(def item-delete-id "delete-item")
(def item-div-id "single-item")

(def next-available-item-index (atom 0))
(def item-indices-for-current-location (atom []))

(defpartial delete-item-props-button [{:keys [label action param id]}]
  [:a.button.delete-item-button {:href "#" :data-action action :data-param param :id id} label])

(defn $item-div [index]
  ($ (str "#" item-div-id index)))

(defn add-fields-for-item [values]
  (swap! next-available-item-index inc)
  (swap! item-indices-for-current-location conj @next-available-item-index)
  (append $item-properties 
          (make-div {:id (str item-div-id @next-available-item-index)}))
  (doall (map #(extract-text-field-and-label (% item-fields-info)
                                             (% values)
                                             ($item-div @next-available-item-index)
                                             @next-available-item-index)
              (keys values)))
  (append ($item-div @next-available-item-index)
          (delete-item-props-button {:label "delete"
                                     :action (str item-delete-id @next-available-item-index)
                                     :param @next-available-item-index
                                     :id (str item-delete-id @next-available-item-index)})))

(def items-sub-property
    {:div-func $item-div
     :indices-atom item-indices-for-current-location
     :next-index-atom next-available-item-index
     :field-adding-func add-fields-for-item
     :location-property :items
     :fields-info item-fields-info
     :delete-button-partial-func delete-item-props-button})

(add-delete-handler-for-location-sub-property items-sub-property)

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
  (show-location-sub-properties location exits-sub-property)
  (show-location-sub-properties location items-sub-property))

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
                :items [{:id "item id" :name "item name" :description "item description" 
                         :countable-noun-prefix "a" :mid-sentence-cased-name "item name cased name"
                         :is-untakeable false :can-be-used-with "nothing" :successful-use-message "success!"
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

(delegate $body locprops-add-exit-button :click
          (fn [event]
            (.preventDefault event)
            (add-fields-for-exit (default-exit))))

(defpartial locprops-add-item-button [{:keys [label action param]}]
  [:a.button.add-item-button {:href "#" :data-action action :data-param param} label])

(append $location-props (locprops-add-item-button {:label "add item"
                                                   :action "add-item"
                                                   :param ""}))

(defn default-item []
  {:id "item id"
   :name "item name"
   :description "item description"
   :countable-noun-prefix "a"
   :mid-sentence-cased-name "item name cased name"
   :is-untakeable false
   :can-be-used-with "nothing"
   :successful-use-message "success!"
   :use-is-not-repeatable false
   :use-actions []})

(delegate $body locprops-add-item-button :click
          (fn [event]
            (.preventDefault event)
            (add-fields-for-item (default-item))))

(defpartial locprops-save-button [{:keys [label action param]}]
  [:a.button.save-button {:href "#" :data-action action :data-param param} label])

(append $location-props (locprops-save-button {:label "save"
                                               :action "save-location"
                                               :param ""}))

(delegate $body locprops-save-button :click
          (fn [event]
            (.preventDefault event)
            (change-location-property (find-current-location) :id (get-value loc-id-field-id))
            (change-location-property (find-current-location) :description (get-value loc-description-field-id))
            (change-location-property (find-current-location) 
                                      :exits
                                      (gather-values-for-sub-property exits-sub-property))
            (change-location-property (find-current-location)
                                      :items
                                      (gather-values-for-sub-property items-sub-property))))
