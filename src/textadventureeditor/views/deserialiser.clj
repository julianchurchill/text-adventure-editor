(ns textadventureeditor.views.deserialiser)

(defn get-exit []
  (let [exit (com.chewielouie.textadventure.LocationExit.)]
    (.setID exit "id")
    (.id exit)))

(defn deserialise-all-locations [text]
  (get-exit))
