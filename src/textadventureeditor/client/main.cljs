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
        [crate.form :only [text-field label check-box]]))

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

(defn get-checked [id]
  (.-checked (dom/getElement id)))

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

(defpartial make-check-box-field [{:keys [name checked value]}]
  (check-box name checked value))

(defpartial make-div [{:keys [id]}]
  [:div {:id id}])

(defpartial make-label [{:keys [name value]}]
  (label name value))

(defn extract-field-and-label [field-info value div next-available-index]
  (append div (make-label {:name (str (:field-id field-info) "-label" next-available-index)
                           :value (:label field-info)}))
  (let [name (str (:field-id field-info) next-available-index)]
    (case (:type field-info)
      :checkbox (append div (make-check-box-field {:name name
                                                   :checked value
                                                   :value (if value "true" "false")}))
      :textfield (append div (make-text-field {:name name
                                               :value value})))))

(defn discard-value [values value]
  (filter #(not (= value %)) values))

(defn get-field-value [id field-info]
  (case (:type field-info)
    :textfield (get-value id)
    :checkbox (get-checked id)))

(defn retrieve-value-from-field [value-key field-info index]
  (let [id (str (:field-id field-info) index)]
    (assoc {} value-key (get-field-value id field-info))))

(defn make-map-from-fields [index fields-info]
  (reduce into (doall (map #(retrieve-value-from-field % (% fields-info) index)
                           (keys fields-info)))))

(defn add-property-fields [property values]
  (let [div-func (:div-func property)
        indices-atom (:indices-atom property)
        next-index-atom (:next-index-atom property)
        location-property (:location-property property)
        fields-info (:fields-info property)
        parent-div (:parent-div property)
        div-base-id (:div-base-id property)]
    (swap! next-index-atom inc)
    (swap! indices-atom conj @next-index-atom)
    (append parent-div
            (make-div {:id (str div-base-id @next-index-atom)}))
    (let [div-elem (div-func @next-index-atom)]
      (let [values-with-matching-fields (select-keys values (keys fields-info))]
        (doall (map #(extract-field-and-label (% fields-info)
                                              (% values)
                                              div-elem
                                              @next-index-atom)
                    (keys values-with-matching-fields)))))))

(defn add-all-property-fields [property values]
  (add-property-fields property values)
  ((:extra-field-adding-func property) values))

(defn show-location-sub-properties [location property]
  (let [div-func (:div-func property)
        indices-atom (:indices-atom property)
        next-index-atom (:next-index-atom property)
        location-property (:location-property property)]
    (doall (map #(remove (div-func %)) @indices-atom))  ; remove all divs for sub-property
    (swap! indices-atom (fn [n] []))                    ; reset indices
    (swap! next-index-atom (fn [n] 0))                  ; reset next index
    (doall (map #(add-all-property-fields property %)
                (location-property location))))) ; convert location property into populated fields

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
  {:id {:field-id "exit-id" :label "exit id" :type :textfield}
   :label {:field-id "exit-label" :label "exit label" :type :textfield}
   :destination {:field-id "exit-destination" :label "exit destination" :type :textfield}
   :direction-hint {:field-id "exit-direction-hint" :label "exit direction hint" :type :textfield}})

(def exit-delete-id "delete-exit")
(def exit-div-id "single-exit")

(def next-available-exit-index (atom 0))
(def exit-indices-for-current-location (atom []))

(defpartial delete-exit-props-button [{:keys [label action param id]}]
  [:a.button.delete-exit-button {:href "#" :data-action action :data-param param :id id} label])

(defn $exit-div [index]
  ($ (str "#" exit-div-id index)))

(defn add-extra-fields-for-exit [values]
  (let [exit-div-elem ($exit-div @next-available-exit-index)]
    (append exit-div-elem
            (delete-exit-props-button {:label "delete"
                                       :action (str exit-delete-id @next-available-exit-index)
                                       :param @next-available-exit-index
                                       :id (str exit-delete-id @next-available-exit-index)}))))

(defn gather-exit-values [property]
  (gather-values-for-sub-property property))

(def exits-sub-property
    {:div-func $exit-div
     :indices-atom exit-indices-for-current-location
     :next-index-atom next-available-exit-index
     :extra-field-adding-func add-extra-fields-for-exit
     :location-property :exits
     :fields-info exit-fields-info
     :delete-button-partial-func delete-exit-props-button
     :parent-div $exit-properties
     :div-base-id exit-div-id
     :value-gatherer-func gather-exit-values})

(add-delete-handler-for-location-sub-property exits-sub-property)

;;;;;;;;;;;;;;;;;;
;; Item actions ;;
;;;;;;;;;;;;;;;;;;

(def item-action-fields-info
  {:action {:field-id "item-action-action-id" :label "item action action" :type :textfield}
   :param {:field-id "item-action-param" :label "item action param" :type :textfield}})

(def item-action-div-id "single-item-action")

(def next-available-item-action-index (atom 0))
(def item-action-indices-for-current-location (atom []))

(defn $item-action-div [index]
  ($ (str "#" item-action-div-id index)))

(defn add-extra-fields-for-item-action [property action]
  (add-property-fields property action))

(defn add-item-actions [property actions]
  (doall (map #((:extra-field-adding-func property) property %) actions)))

(defn gather-item-action-values [property]
  (gather-values-for-sub-property property))

(def item-actions-sub-property
    {:div-func $item-action-div
     :indices-atom item-action-indices-for-current-location
     :next-index-atom next-available-item-action-index
     :extra-field-adding-func add-extra-fields-for-item-action
;     :location-property :use-actions
     :fields-info item-action-fields-info
;     :delete-button-partial-func delete-item-action-props-button
;     :parent-div $item-action-properties
     :div-base-id item-action-div-id
     :value-gatherer-func gather-item-action-values})

;;;;;;;;;;;
;; Items ;;
;;;;;;;;;;;

(def $item-properties ($ :#item-properties))

(def item-fields-info
  {:id {:field-id "item-id" :label "item id" :type :textfield}
   :name {:field-id "item-name" :label "item name" :type :textfield}
   :description {:field-id "item-description" :label "item description" :type :textfield}
   :countable-noun-prefix {:field-id "item-countable-noun-prefix" :label "item countable noun prefix" :type :textfield}
   :mid-sentence-cased-name {:field-id "item-mid-sentence-cased-name" :label "item mid sentence cased name" :type :textfield}
   :is-untakeable {:field-id "item-is-untakeable" :label "item is untakeable" :type :checkbox}
   :can-be-used-with {:field-id "item-can-be-used-with" :label "item can be used with" :type :textfield}
   :successful-use-message {:field-id "item-successful-use-message" :label "item successful use message" :type :textfield}
   :use-is-not-repeatable {:field-id "item-use-is-not-repeatable" :label "item use is not repeatable" :type :checkbox}})

(def item-delete-id "delete-item")
(def item-div-id "single-item")

(def next-available-item-index (atom 0))
(def item-indices-for-current-location (atom []))

(defpartial delete-item-props-button [{:keys [label action param id]}]
  [:a.button.delete-item-button {:href "#" :data-action action :data-param param :id id} label])

(defn $item-div [index]
  ($ (str "#" item-div-id index)))

(defn add-extra-fields-for-item [values]
  (let [item-div-elem ($item-div @next-available-item-index)]
    (append item-div-elem
            (delete-item-props-button {:label "delete"
                                       :action (str item-delete-id @next-available-item-index)
                                       :param @next-available-item-index
                                       :id (str item-delete-id @next-available-item-index)}))
    (add-item-actions (assoc item-actions-sub-property :parent-div item-div-elem)
                      (:use-actions values))))

(defn gather-item-values [property]
  (gather-values-for-sub-property property))

(def items-sub-property
    {:div-func $item-div
     :indices-atom item-indices-for-current-location
     :next-index-atom next-available-item-index
     :extra-field-adding-func add-extra-fields-for-item
     :location-property :items
     :fields-info item-fields-info
     :delete-button-partial-func delete-item-props-button
     :parent-div $item-properties
     :div-base-id item-div-id
     :value-gatherer-func gather-item-values})

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
                         :use-is-not-repeatable false
                         :use-actions [{:action :change-item-description :param "It is unlocked."}
                                       {:action :change-item-name :param "unlocked door"}
                                       {:action :make-exit-visible :param "clocktowerdoor"}]}]})
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
            (add-all-property-fields exits-sub-property (default-exit))))

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
            (add-all-property-fields items-sub-property (default-item))))

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
                                      (:location-property exits-sub-property)
                                      ((:value-gatherer-func exits-sub-property) exits-sub-property))
            (change-location-property (find-current-location)
                                      (:location-property items-sub-property)
                                      ((:value-gatherer-func items-sub-property) items-sub-property))))
