(ns textadventureeditor.views.deserialiser)

(import '(com.chewielouie.textadventure BasicModel
                                        Exit
                                        LocationExitFactory
                                        LocationFactory
                                        PlainTextModelPopulator))
(import '(com.chewielouie.textadventure.item NormalItemFactory))
(import '(com.chewielouie.textadventure.itemaction NormalItemActionFactory))
(import '(com.chewielouie.textadventure.serialisation PlainTextItemDeserialiser
                                                      PlainTextExitDeserialiser
                                                      PlainTextModelLocationDeserialiser))

(defn make-item [item]
  {:id (.id item)
   :name (.name item)
   :description (.description item)
   :countable-noun-prefix (.countableNounPrefix item)
   :mid-sentence-cased-name (.midSentenceCasedName item)
   :is-untakeable (not (.takeable item))
   :can-be-used-with "nothing"
   :successful-use-message (.usedWithText item)
   :use-is-not-repeatable false
   :use-actions [
;                 {:action :change-item-description :param "It is unlocked."}
   ]})

(defn convert-items [items]
  (doall (map #(make-item %) items)))

(defn make-exit [exit]
  {:id (.id exit)
   :label (.label exit)
   :destination (.destination exit)
   :is-not-visible (not (.visible exit))
   :direction-hint (case (.directionHint exit) 
                     com.chewielouie.textadventure.Exit$DirectionHint/North "North"
                     com.chewielouie.textadventure.Exit$DirectionHint/South "South"
                     com.chewielouie.textadventure.Exit$DirectionHint/East "East"
                     com.chewielouie.textadventure.Exit$DirectionHint/West "West"
                     com.chewielouie.textadventure.Exit$DirectionHint/DontCare "DontCare"
                     "UNKNOWN")})

(defn convert-exits [exits]
  (doall (map #(make-exit %) exits)))

(defn make-location [loc]
  {:id (.id loc)
   :description (.description loc)
   :exits (convert-exits (.exitsIncludingInvisibleOnes loc))
   :items (convert-items (.items loc))})

(defn convert-locations-to-clojure-map [locations]
  (doall (map #(make-location %) locations)))

(defn deserialise-all-locations [text]
  (let [model (BasicModel.)
        item-action-factory (NormalItemActionFactory. model)
        item-deserialiser (PlainTextItemDeserialiser. item-action-factory)
        item-factory (NormalItemFactory.)
        location-exit-factory (LocationExitFactory.)
        exit-deserialiser (PlainTextExitDeserialiser.)
        location-deserialiser (PlainTextModelLocationDeserialiser. item-factory
                                                                   location-exit-factory
                                                                   item-deserialiser
                                                                   exit-deserialiser)
        location-factory (LocationFactory. model)
        model-populator (PlainTextModelPopulator. model
                                                  location-factory
                                                  model
                                                  item-factory
                                                  location-deserialiser
                                                  item-deserialiser
                                                  text)]
    (convert-locations-to-clojure-map [(.currentLocation model)])))
;    (convert-locations-to-clojure-map [(.locations model)])))
